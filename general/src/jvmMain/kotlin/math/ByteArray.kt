package math

import kotlin.experimental.xor

infix fun ByteArray.xor(b : ByteArray) : ByteArray{
    return ByteArray(size){
        this[it] xor b[it]
    }
}

infix fun List<Byte>.xor(b : List<Byte>) : List<Byte>{
    return List(size){
        this[it] xor b[it]
    }
}