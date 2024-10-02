package com.suygecu.testpepsa;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class InSorrowPacket {

    public abstract void writePacket(DataOutput output) throws IOException;

    public abstract void readPacket(DataInput input) throws IOException;

    public abstract void processPacket();
}

