package com.ibn;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.runtime.*;
import jcuda.driver.*;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static jcuda.driver.JCudaDriver.*;

/**
 * Created by madhusudanan on 19/08/16.
 */
public class JCUDAExample2 {



    public static void main(String args[]) {
       class myPointer extends CUdeviceptr {
            myPointer() {
                super();
            }
            public long getBuffer() {
                return getNativePointer();
            }
        }

        int numElements = 10;
        long hostInput[] = new long[numElements];
        long hostOutput[] = new long[numElements];

        for(int i = 0; i < numElements; i++)
            hostInput[i] = (long)i;

        // Enable exceptions and omit all subsequent error checks
        JCudaDriver.setExceptionsEnabled(true);

        // Initialize the driver and create a context for the first device.
        cuInit(0);
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        CUcontext context = new CUcontext();
        cuCtxCreate(context, 0, device);

        // Load the ptx file.
        CUmodule module = new CUmodule();
        cuModuleLoad(module, "/home/kmadhu/DSDebug/src/main/resources/cudaSamples.ptx");

        // Obtain a function pointer to the "add" function.
        CUfunction function = new CUfunction();
        cuModuleGetFunction(function, module, "square");


        // Allocate the device hostinput data, and copy the
        // host hostinput data to the device
        myPointer myptr = new myPointer();
        cuMemAllocHost(myptr,80);
        ByteBuffer buf = myptr.getByteBuffer(0,80).order(ByteOrder.LITTLE_ENDIAN);
        for(int i = 0; i < numElements; i++)
            buf.putLong(i*8,i*10);

        // COpy hostinput to Device
        CUdeviceptr deviceinput = new CUdeviceptr();
        cuMemAlloc(deviceinput, numElements * Sizeof.LONG);
        cuMemcpyHtoD(deviceinput,
                Pointer.to(buf),
                numElements * Sizeof.LONG);


        CUdeviceptr deviceoutput = new CUdeviceptr();
        cuMemAlloc(deviceoutput, numElements * Sizeof.LONG);

        System.out.println("host Input buf = " + buf);
        System.out.println("host Input Pointer.to(buf) = " + Pointer.to(buf));
        System.out.println("host Input myptr = " + myptr);
        System.out.println("device Input " + deviceinput);
        System.out.println("device output" + deviceoutput);

        // Set up the kernel parameters: A pointer to an array
        // of pointers which point to the actual values.
        Pointer kernelParameters = Pointer.to(
                Pointer.to(new int[]{numElements}),
                Pointer.to(deviceinput),
                Pointer.to(deviceoutput)
        );

        // Call the kernel function.
        int blockSizeX = 256;
        int gridSizeX = (int) Math.ceil((double) numElements / blockSizeX);
        cuLaunchKernel(function,
                gridSizeX, 1, 1,      // Grid dimension
                blockSizeX, 1, 1,      // Block dimension
                0, null,               // Shared memory size and stream
                kernelParameters, null // Kernel- and extra parameters
        );

        cuCtxSynchronize();

        // Allocate host output memory and copy the device output
        // to the host.
        cuMemcpyDtoH(Pointer.to(hostOutput), deviceoutput, numElements * Sizeof.LONG);

        // Clean up.
        cuMemFreeHost(myptr);
        cuMemFree(deviceinput);
        cuMemFree(deviceoutput);

    }
}
