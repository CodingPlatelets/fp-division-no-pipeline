package fpDivision

import chisel3._
import chisel3.util._

class fpDiv(val w: Int = 32) extends Module {
  val io = IO(new Bundle {
    val in1 = Input(UInt(width = w.W))
    val in2 = Input(UInt(width = w.W))
    val out = Output(UInt(width = w.W))
  })

  val inverter = Module(new fpInverter(23))
  val multiplier = Module(new FPMult(w))

  inverter.io.in1 := io.in2(22, 0) //mantissa
  //val exponent2    = io.in2(30, 23) - 127.U
  //val negExpTmp    = 127.U - exponent2		// invert the sign of exponent
  val negExpTmp = 254.U - io.in2(30, 23)
  val invMant = inverter.io.out(23, 0)
  val negExp = Mux(invMant === 0.U, negExpTmp, negExpTmp - 1.U)

  // we should raise an execption if both mantissa and exponent are zero (the final result should be inf

  //val in1Buffer    = Reg(init = 0.U, next = io.in1)

  multiplier.io.a := io.in1
  multiplier.io.b := Cat(io.in2(31), negExp, inverter.io.out(23, 1))
  // skipping msb of inverter (multiplying mantissa by 2)

  io.out := multiplier.io.res

  // printf("\nmultiplier b : %d\n", Cat(io.in2(31), negExp, inverter.io.out(23, 1)))
  // printf("negExp : %d, in2 exp %d\n", negExp, io.in1(30, 23))
  // printf("in1: %d, in2: %d\n", io.in1, io.in2)
  // printf("inverter in: %d out: %d\n", io.in2(22, 0), inverter.io.out)
  // printf("out: %d\n", multiplier.io.res)

//	printf("%d\n", multiplier.io.res)

}

// object fpDiv {
//   def main(args: Array[String]): Unit = {
//     if (!Driver(() => new fpDiv(32))(c => new fpDivTest(c))) System.exit(1)
//   }
// }
