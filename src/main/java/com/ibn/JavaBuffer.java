/**
 * Created by madhusudanan on 19/08/16.
 */

package com.ibn;

import java.nio.*;

public class JavaBuffer {


    public static void main(String args[]) {

        System.out.println("Hello");
        ByteBuffer buffer = ByteBuffer.allocateDirect(Long.SIZE * 3).order(ByteOrder.LITTLE_ENDIAN);

        buffer.putLong(11234444);
        buffer.putLong(3443434343L);
        buffer.putLong(10);

        buffer.rewind();
        System.out.println("Getting" + buffer.getLong());
        System.out.println("Getting it" + buffer.getLong());
        System.out.println("Getting" + buffer.getLong());

        System.out.println("direct =" + buffer.isDirect());
        System.out.println("array =" + buffer.hasArray());

    }
}
