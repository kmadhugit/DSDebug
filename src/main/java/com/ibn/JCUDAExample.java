package com.ibn;

import jcuda.Pointer;
import jcuda.runtime.JCuda;

/**
 * Created by madhusudanan on 19/08/16.
 */
public class JCUDAExample {

        public static void main(String args[]) {
            Pointer pointer = new Pointer();
            JCuda.cudaMalloc(pointer, 4);
            System.out.println("Pointer: "+pointer);
            JCuda.cudaFree(pointer);
        }
}
