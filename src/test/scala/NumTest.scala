package fpDivision

import chisel3._
import chisel3.util.Cat
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

import scala.util.Random

class NumsSub extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(4.W))
    val b = Input(UInt(4.W))
    val res = Output(UInt(5.W))
  })

  io.res := Cat(0.U(1.W), io.a) - Cat(0.U(1.W), io.b)
}

class NumTest extends AnyFlatSpec with ChiselScalatestTester {
  val annos = Seq(VerilatorBackendAnnotation)

  behavior.of("tester on nums adder")
  it should "two UInt sub" in {
    test(new NumsSub)
      .withAnnotations(annos) { dut =>
        dut.reset.poke(true.B)
        dut.clock.step(1)
        dut.reset.poke(false.B)

        // the inputs are Complement
        dut.io.a.poke("b0111".U)
        dut.io.b.poke("b1101".U)

        // the result is 0111 + 1101 = 11010
        print(s"the resutl is: ${dut.io.res.peek().litValue}\n")

      }
  }
}
