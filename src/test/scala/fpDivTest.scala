package fpDivision

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class fpDivTest extends AnyFlatSpec with ChiselScalatestTester {

  val bit = 64
  val dimV = 32
  val depth = 128
  val annos = Seq(VerilatorBackendAnnotation)

  behavior.of("tester on fifo with memory")
  it should "fifo with it" in {
    test(new fpDiv(32))
      .withAnnotations(annos) { dut =>
        /*
	poke(c.io.in1, 1096155136.U)		// 13.75
	poke(c.io.in2, 1091829760.U)		// 9.25

	step(1)
	expect(c.io.out, 1123512320.U)		//123.1875
	step(1)
         */
        dut.reset.poke(true.B)
        dut.clock.step(1)
        dut.reset.poke(false.B)
        dut.clock.step(1)

        dut.io.in1.poke(1065353216.U) // 65536
        dut.io.in2.poke(0.U) // 0
        dut.clock.step(1)
        dut.io.out.expect(0.U)

        dut.io.in1.poke(2139095039.U)
        dut.io.in2.poke(2139095039.U)
        dut.clock.step(1)
        dut.io.out.expect(0.U)

        dut.io.in1.poke(1199570944.U) // 65536
        dut.io.in2.poke(1106771968.U) // 31
        dut.clock.step(1)
        dut.io.out.expect(0.U)

        // dut.io.in2.poke(1098383360.U) // 15.5
        dut.io.in1.poke(2139095039.U) // biggest single precision float
        dut.io.in2.poke(1207435264.U)
        dut.clock.step(1)
        dut.io.out.expect(0.U)

        dut.io.in1.poke(2139095039.U)
        dut.io.in2.poke(1200035266.U)
        dut.clock.step(1)
        dut.io.out.expect(0.U)

        dut.io.in1.poke(1082130431.U) // exp = 1, mantissa = 28 ones
        dut.io.in2.poke(1106771968.U) // 31
        dut.clock.step(1)
        dut.io.out.expect(0.U)

        dut.io.in1.poke(1207435265.U)
        dut.io.in2.poke(2139095039.U)
        dut.clock.step(1)
        dut.io.out.expect(0.U)
        dut.clock.step(1)
        dut.io.out.expect(0.U)

        dut.io.in1.poke(1207959552.U)
        // dut.io.in1.poke(1200035266.U)
        dut.clock.step(1)
        dut.io.out.expect(0.U)
        dut.clock.step(1)
        dut.io.out.expect(0.U)

        // dut.io.in2.poke(0.U)

        // 1125056512 = 143.0
        // 1199570944 = 65536
        // 1207959552 = 131072
        // 1106771968 = 31
        /*
        for (i <- 1199570944 until 1207959552) {
        // for (i <- 1199570944 until 1200095232) {
            dut.io.in1.poke(i.U)
            dut.io.in2.poke(1106771968.U)
            dut.clock.step(1)
        }
         */

        dut.io.out.expect(0.U)
        dut.clock.step(1)
        dut.io.out.expect(0.U)
        dut.clock.step(1)
        dut.io.out.expect(0.U)
        dut.clock.step(1)
        dut.io.out.expect(0.U)
        dut.clock.step(1)
        dut.io.out.expect(0.U)
        dut.clock.step(1)
        dut.io.out.expect(0.U)

      }
  }
}
