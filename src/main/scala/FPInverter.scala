package fpDivision

import chisel3._
import chisel3.util._

class FPInverter(val width: Int) extends Module {
  val io = IO(new Bundle {
    val in1 = Input(UInt(width.W))
    val out = Output(UInt((width + 1).W))

  })

  // instantiate lookup tables
  val tableC = Module(new lookupC)
  val tableL = Module(new lookupL)
  val tableJ = Module(new lookupJ)

  // Most significant 9 bits of the input (mantissa) is used as address
  // to the coefficient lookup tables
  val coeffAddr = io.in1(width - 1, width - 6)
  tableC.io.addr := coeffAddr
  tableL.io.addr := coeffAddr
  tableJ.io.addr := coeffAddr

  val sub1 = WireDefault(0.U(width.W))
  sub1 := (io.in1 ^ Fill(23, "b1".U)) + 1.U

  val mul1 = Module(new VarSizeMul(23, 17, 24))
  mul1.io.in1 := tableJ.io.out
  mul1.io.in2 := io.in1(width - 7, 0)
  // result will be input to adder1

  val mul2 = Module(new mul2(24, 17, 29))
  mul2.io.in1 := tableC.io.out
  mul2.io.in2 := (io.in1(width - 7, 0) * io.in1(width - 7, 0))(33, 17)
  // result will be input to sub2

//	val adder = Module(new VarSizeAdder(17, 11, 17))
// using sub due to the sign value of the j coefficients
  val sub2_in2 = WireDefault(0.U(24.W))
  sub2_in2 := (mul1.io.out ^ Fill(24, "b1".U)) + "b1".U
  val append = WireDefault(0.U(3.W))
  val sub2 = Module(new VarSizeSub(27, 27, 27))
  sub2.io.in1 := tableL.io.out
  val temp3 = Cat(sub2_in2, "b0".U)
  val temp4 = Cat(temp3, "b0".U)
  sub2.io.in2 := Cat(temp4, "b0".U)
  // result will be input to sub2

//	val sub2 = Module(new VarSizeSub(17, 5, 17))
//	using an adder due to the sign value of the c coefficients
  val adder = Module(new VarSizeAdder(29, 29, 25))
  val temp1 = Cat(sub2.io.out, "b0".U)
  val temp2 = Cat(temp1, "b0".U)
  adder.io.in1 := temp2
  adder.io.in2 := mul2.io.out
  // result will be input to mul3

  val mul3 = Module(new mul3(width, 25, 24))
  mul3.io.in1 := sub1 //sub1.io.out
  mul3.io.in2 := adder.io.out

  //val outReg = Reg(init = UInt(0, width = w + 1), next = mul3.io.out)
  //io.out := outReg

  io.out := mul3.io.out
}
