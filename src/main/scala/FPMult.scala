// Takes 2 clock cyles to produce the result

package fpDivision

import chisel3._
import chisel3.util.Cat

class MantissaRounder(val n: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(n.W))
    val out = Output(UInt((n - 1).W))
  })

  io.out := io.in(n - 1, 1) // + io.in(0)
}

class FPMult(val width: Int) extends Module {
  val io = IO(new Bundle {
    val multiplicand = Input(UInt(width.W))
    val multiplier = Input(UInt(width.W))
    val res = Output(UInt(width.W))
  })

  val multiplicandWrap = new FloatWrapper(io.multiplicand)
  val multiplierWrap = new FloatWrapper(io.multiplier)

  val stage1_sign = multiplicandWrap.sign ^ multiplierWrap.sign
  val stage1_exponent = multiplicandWrap.exponent + multiplierWrap.exponent
  val stage1_mantissa = multiplicandWrap.mantissa * multiplierWrap.mantissa
  val stage1_zero = multiplicandWrap.zero || multiplierWrap.zero

  val stage2_sign = stage1_sign
  val stage2_exponent = WireDefault(0.U(multiplicandWrap.exponent.getWidth.W))
  val stage2_mantissa = WireDefault(0.U((multiplicandWrap.mantissa.getWidth - 1).W))

  val (mantissaLead, mantissaSize, exponentSize, exponentSub) = width match {
    case 32 => (47, 23, 8, 127)
    case 64 => (105, 52, 11, 1023)
  }

  val rounder = Module(new MantissaRounder(mantissaSize + 1))

  when(stage1_zero) {
    stage2_exponent := 0.U(exponentSize.W)
    rounder.io.in := 0.U((mantissaSize + 1).W)
  }.elsewhen(stage1_mantissa(mantissaLead) === 1.U(1.W)) {
    stage2_exponent := stage1_exponent - (exponentSub - 1).asUInt
    rounder.io.in := stage1_mantissa(mantissaLead - 1, mantissaLead - mantissaSize - 1)
  }.otherwise {
    stage2_exponent := stage1_exponent - (exponentSub).asUInt
    rounder.io.in := stage1_mantissa(mantissaLead - 2, mantissaLead - mantissaSize - 2)
  }

  stage2_mantissa := rounder.io.out

  io.res := Cat(stage2_sign.asUInt, stage2_exponent.asUInt, stage2_mantissa.asUInt)
}

class FPMult32 extends FPMult(32) {}
class FPMult64 extends FPMult(64) {}
