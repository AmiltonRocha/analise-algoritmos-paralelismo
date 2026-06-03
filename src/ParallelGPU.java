import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jocl.*;
import static org.jocl.CL.*;

class ParallelGPU {
   public static void main(String[] args) {
    if (args.length < 2) {
 System.err.println("Uso: java ParallelGPU <arquivo.txt> <palavra>");
 return;
   }
   String caminhoArquivo = args[0];
   String palavra = args[1];
   
   if (palavra.isEmpty()) {
    System.err.println("A palavra nao pode ser vazia");
    return;

    }

      File arquivo = new File(caminhoArquivo);
if (!arquivo.exists() || !arquivo.isFile()) {
    System.err.println("Arquivo nao encontrado: " + caminhoArquivo);
    return;
}
 try{
   String texto = Files.readString(Path.of(caminhoArquivo), StandardCharsets.UTF_8);
byte[] textoBytes = texto.getBytes(StandardCharsets.UTF_8);
byte[] palavraBytes = palavra.toLowerCase().getBytes(StandardCharsets.UTF_8);
int palavraLen = palavraBytes.length;

   // Inicializa OpenCL
   cl_platform_id[] platforms = new cl_platform_id[1];
   clGetPlatformIDs(1, platforms, null);
   cl_platform_id platform = platforms[0];

   cl_device_id[] devices = new cl_device_id[1];
   clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, devices, null);
   cl_device_id device = devices[0];

   cl_context context = clCreateContext(null, 1, new cl_device_id[]{device}, null, null, null);
   cl_command_queue queue = clCreateCommandQueue(context, device, 0, null);

String kernelSource = 
    "__kernel void contar(__global const char* texto, int textoLen," +
    "                     __global const char* palavra, int palavraLen," +
    "                     __global int* resultado) {" +
    "    int id = get_global_id(0);" +
    "    if (id >= textoLen) return;" +
    "    int match = 1;" +
    "    if (id + palavraLen > textoLen) match = 0;" +
    "    else {" +
    "        for (int i = 0; i < palavraLen; i++) {" +
    "            if (texto[id + i] != palavra[i]) { match = 0; break; }" +
    "        }" +
    "    }" +
    "    if (match) {" +
    "        atomic_inc(resultado);" +
    "    }" +
    "}";

   cl_program program = clCreateProgramWithSource(context, 1, new String[]{kernelSource}, null, null);
   clBuildProgram(program, 0, null, null, null, null);
   cl_kernel kernel = clCreateKernel(program, "contar", null);

   // Buffers na GPU
   cl_mem textoMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, (long)textoBytes.length, Pointer.to(textoBytes), null);
   cl_mem palavraMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, (long)palavraBytes.length, Pointer.to(palavraBytes), null);
   cl_mem resultadoMem = clCreateBuffer(context, CL_MEM_READ_WRITE, (long)Sizeof.cl_int, null, null);

   // Zera o resultado na GPU
   int[] zero = {0};
   clEnqueueWriteBuffer(queue, resultadoMem, CL_TRUE, 0, Sizeof.cl_int, Pointer.to(zero), 0, null, null);

   // Argumentos do kernel
   clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(textoMem));
   clSetKernelArg(kernel, 1, Sizeof.cl_int, Pointer.to(new int[]{textoBytes.length}));
   clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(palavraMem));
   clSetKernelArg(kernel, 3, Sizeof.cl_int, Pointer.to(new int[]{palavraLen}));
   clSetKernelArg(kernel, 4, Sizeof.cl_mem, Pointer.to(resultadoMem));

   // Executa o kernel
   long inicioNs = System.nanoTime();
   long globalSize = textoBytes.length;
   clEnqueueNDRangeKernel(queue, kernel, 1, null, new long[]{globalSize}, null, 0, null, null);
   clFinish(queue);
   long fimNs = System.nanoTime();

   // Le o resultado
   int[] totalArray = new int[1];
   clEnqueueReadBuffer(queue, resultadoMem, CL_TRUE, 0, Sizeof.cl_int, Pointer.to(totalArray), 0, null, null);
   long ms = (fimNs - inicioNs) / 1_000_000L;

   System.out.println("ParallelGPU: " + totalArray[0] + " ocorrencias em " + ms + " ms");

   // Limpeza
   clReleaseMemObject(textoMem);
   clReleaseMemObject(palavraMem);
   clReleaseMemObject(resultadoMem);
   clReleaseKernel(kernel);
   clReleaseProgram(program);
   clReleaseCommandQueue(queue);
   clReleaseContext(context);

   } catch (Exception e) {
    System.err.println("Erro no ParallelGPU: " + e.getMessage());
   }
  }
}
