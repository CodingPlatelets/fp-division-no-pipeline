package fputilNoPipe

import chisel3._
import chisel3.util._

class FCMA(val width: Int) extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(width.W))
    val b = Input(UInt(width.W))
    val c = Input(UInt(width.W))
    val res = Output(UInt(width.W))
  })

  val fpAddModule = Module(new FPAdd(width))
  val fpMulModule = Module(new FPMult(width))

  fpMulModule.io.multiplicand <> io.a
  fpMulModule.io.multiplier <> io.b

  fpAddModule.io.a <> io.c
  fpAddModule.io.b <> fpMulModule.io.res

  io.res := fpAddModule.io.res
}