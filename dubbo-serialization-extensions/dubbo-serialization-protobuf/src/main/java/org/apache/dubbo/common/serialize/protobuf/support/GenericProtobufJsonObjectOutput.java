/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.serialize.protobuf.support;

import org.apache.dubbo.common.serialize.ObjectOutput;
import org.apache.dubbo.common.serialize.protobuf.support.wrapper.MapValue;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.FloatValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static org.apache.dubbo.common.constants.CommonConstants.HEARTBEAT_EVENT;
import static org.apache.dubbo.common.constants.CommonConstants.MOCK_HEARTBEAT_EVENT;

/**
 * GenericGoogleProtobuf object output implementation
 */
public class GenericProtobufJsonObjectOutput implements ObjectOutput {

    private final PrintWriter writer;

    public GenericProtobufJsonObjectOutput(OutputStream out) {
        this.writer = new PrintWriter(new OutputStreamWriter(out));
    }

    @Override
    public void writeBool(boolean v) throws IOException {

        writeObject(BoolValue.newBuilder().setValue(v).build());
    }

    @Override
    public void writeByte(byte v) throws IOException {
        writeObject(Int32Value.newBuilder().setValue((v)).build());
    }

    @Override
    public void writeShort(short v) throws IOException {
        writeObject(Int32Value.newBuilder().setValue(v).build());
    }

    @Override
    public void writeInt(int v) throws IOException {
        writeObject(Int32Value.newBuilder().setValue(v).build());
    }

    @Override
    public void writeLong(long v) throws IOException {
        writeObject(Int64Value.newBuilder().setValue(v).build());
    }

    @Override
    public void writeFloat(float v) throws IOException {
        writeObject(FloatValue.newBuilder().setValue(v).build());
    }

    @Override
    public void writeDouble(double v) throws IOException {
        writeObject(DoubleValue.newBuilder().setValue(v).build());
    }

    @Override
    public void writeUTF(String v) throws IOException {
        writeObject(StringValue.newBuilder().setValue(v).build());
    }

    @Override
    public void writeBytes(byte[] b) throws IOException {
        writeObject(BytesValue.newBuilder().setValue(ByteString.copyFrom(b)).build());
    }

    @Override
    public void writeBytes(byte[] b, int off, int len) throws IOException {
        writeObject(BytesValue.newBuilder().setValue(ByteString.copyFrom(b, off, len)).build());
    }


    @Override
    public void writeObject(Object obj) throws IOException {
        if (obj == null) {
            throw new IllegalArgumentException("This serialization only support google protobuf object, the object is : null");
        }
        if (!ProtobufUtils.isSupported(obj.getClass())) {
            throw new IllegalArgumentException("This serialization only support google protobuf object, the object class is: " + obj.getClass().getName());
        }

        writer.write(ProtobufUtils.serializeJson(obj));
        writer.println();
        writer.flush();
    }

    @Override
    public void writeThrowable(Object th) throws IOException {
        if (th instanceof Throwable && !ProtobufUtils.isSupported(th.getClass())) {
            th = ProtobufUtils.convertToThrowableProto((Throwable) th);
        }
        writer.write(ProtobufUtils.serializeJson(th));
        writer.println();
        writer.flush();
    }

    @Override
    public void writeEvent(Object data) throws IOException {
        if (data == HEARTBEAT_EVENT) {
            data = MOCK_HEARTBEAT_EVENT;
        }
        writeUTF((String) data);
    }

    /**
     * FIXME, only supports transmission of String values.
     *
     * @param attachments
     * @throws IOException
     */
    @Override
    public void writeAttachments(Map<String, Object> attachments) throws IOException {
        if (attachments == null) {
            return;
        }

        Map<String, String> stringAttachments = new HashMap<>();
        attachments.forEach((k, v) -> stringAttachments.put(k, (String) v));
        MapValue.Map proto = MapValue.Map.newBuilder().putAllAttachments(stringAttachments).build();
        writer.write(ProtobufUtils.serializeJson(proto));
        writer.println();
        writer.flush();
    }

    @Override
    public void flushBuffer() {
        writer.flush();
    }

}
