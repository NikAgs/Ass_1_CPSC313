package arch.y86.machine.seq.student;

import arch.y86.machine.AbstractY86CPU;
import machine.AbstractMainMemory;
import machine.Register;
import machine.RegisterSet;

public class CPU extends AbstractY86CPU.Sequential {

    public CPU(String name, AbstractMainMemory memory) {
        super(name, memory);
    }

    /**
     * Execute one clock cycle with all stages executing in parallel.
     *
     * @throws InvalidInstructionException                if instruction is invalid (including invalid register number).
     * @throws AbstractMainMemory.InvalidAddressException if instruction attemps an invalid memory access (either instruction or data).
     * @throws AbstractMainMemory.OutOfMemoryException    if too many 1K pages have been accessed by the program.
     * @throws MachineHaltException                       if instruction halts the CPU.
     * @throws Register.TimingException                   if a stage tries to access a stage register that was never written to.
     * @throws ImplementationException                    if an internal error (usually caused by a student bug) occurs.
     */
    @Override
    protected void cycle() throws InvalidInstructionException, AbstractMainMemory.InvalidAddressException, AbstractMainMemory.OutOfMemoryException, MachineHaltException, Register.TimingException, ImplementationException {
        cycleSeq();
    }

    /**
     * The FETCH stage of CPU
     *
     * @throws Register.TimingException if a stage tries to access a stage register that was never written to.
     */
    @Override
    protected void fetch() throws Register.TimingException {
        try {

            // get iCd and iFn
            f.iCd.set(mem.read(F.pc.get(), 1)[0].value() >>> 4);
            f.iFn.set(mem.read(F.pc.get(), 1)[0].value() & 0xf);

            // stat MUX
            switch (f.iCd.getValueProducedInt()) {
                case I_HALT:
                case I_NOP:
                case I_IRMOVQ:
                case I_RET:
                case I_PUSHQ:
                case I_POPQ:	
                case I_RMMOVQ:
                case I_MRMOVQ:
                    switch (f.iFn.getValueProducedInt()) {
                        case 0x0:
                            f.stat.set(S_AOK);
                            break;
                        default:
                            f.stat.set(S_INS);
                            break;
                    }
                    break;
                case I_RRMVXX:
                case I_JXX:
                    switch (f.iFn.getValueProducedInt()) {
                        case C_NC:
                        case C_LE:
                        case C_L:
                        case C_E:
                        case C_NE:
                        case C_GE:
                        case C_G:
                            f.stat.set(S_AOK);
                            break;
                        default:
                            f.stat.set(S_INS);
                    }
                    break;
                case I_OPQ:
                    switch (f.iFn.getValueProducedInt()) {
                        case A_ADDQ:
                        case A_SUBQ:
                        case A_ANDQ:
                        case A_XORQ:
                        case A_MULQ:
                        case A_DIVQ:
                        case A_MODQ:
                            f.stat.set(S_AOK);
                            break;
                        default:
                            f.stat.set(S_INS);
                            break;
                    }
                    break;
		//----------------------------						//6.1 Fetch
		case I_IOPQ:
                    switch (f.iFn.getValueProducedInt()) {
                        case A_ADDQ:
                        case A_SUBQ:
                        case A_ANDQ:
                        case A_XORQ:
                        case A_MULQ:
                        case A_DIVQ:
                        case A_MODQ:
                            f.stat.set(S_AOK);
                            break;
                        default:
                            f.stat.set(S_INS);
                            break;
                    }
		    break;
		//----------------------------	
                case I_CALL:								//6.2 Added new case iFn=9
		    switch (f.iFn.getValueProducedInt()) {
                        case 0x0:
			case 0x9:
                            f.stat.set(S_AOK);
                            break;
                        default:
                            f.stat.set(S_INS);
                            break;
                    }
		    break;
		//---------------------------
                default:
                    f.stat.set(S_INS);
                    break;
            }

            if (f.stat.getValueProducedInt() == S_AOK) {

                // rA MUX
                switch (f.iCd.getValueProducedInt()) {
                    case I_HALT:
                        f.rA.set(R_NONE);
                        f.stat.set(S_HLT);
                        break;
                    case I_RRMVXX:
                    case I_RMMOVQ:
                    case I_MRMOVQ:
                    case I_OPQ:
                    case I_PUSHQ:
                    case I_POPQ:
                        f.rA.set(mem.read(F.pc.get() + 1, 1)[0].value() >>> 4);
                        break;
		    case I_CALL:							//6.2 set rA only for iFn=9
		    	switch (f.iFn.getValueProducedInt()) {
			    case 0x9:
                        	f.rA.set(mem.read(F.pc.get() + 1, 1)[0].value() >>> 4);
				break;
			    default:
				f.rA.set(R_NONE);
				break;
                   	}
			break;
                    default:
                        f.rA.set(R_NONE);
                }

                // rB MUX
                switch (f.iCd.getValueProducedInt()) {
                    case I_RRMVXX:
                    case I_IRMOVQ:
                    case I_RMMOVQ:
                    case I_MRMOVQ:
                    case I_OPQ:
		    case I_IOPQ:							//6.1 rB
                        f.rB.set(mem.read(F.pc.get() + 1, 1)[0].value() & 0xf);
                        break;
		    case I_CALL:
		    	switch (f.iFn.getValueProducedInt()) {				//6.2 set rB only for iFn=9
			    case 0x9:
                        	f.rB.set(mem.read(F.pc.get() + 1, 1)[0].value() & 0xf);
				break;
			    default:
				f.rA.set(R_NONE);
				break;
                   	}
			break;
                    default:
                        f.rB.set(R_NONE);
                }

                // valC MUX
                switch (f.iCd.getValueProducedInt()) {
                    case I_IRMOVQ:
                    case I_RMMOVQ:
                    case I_MRMOVQ:
		    case I_IOPQ:							//6.1 valC
                        f.valC.set(mem.readLongUnaligned(F.pc.get() + 2));
                        break;
                    case I_CALL:
		    	switch (f.iFn.getValueProducedInt()) {				//6.2 set valC based on iFn
			    case 0x9:
                         	f.valC.set(mem.readLongUnaligned(F.pc.get() + 2));
				break;
			    default:
                        	f.valC.set(mem.readLongUnaligned(F.pc.get() + 1));
				break;
                   	}
                        break;
                    case I_JXX:
                        f.valC.set(mem.readLongUnaligned(F.pc.get() + 1));
			break;
                    default:
                        f.valC.set(0);
                }

                // valP MUX
                switch (f.iCd.getValueProducedInt()) {
                    case I_NOP:
                    case I_HALT:
                    case I_RET:
                        f.valP.set(F.pc.get() + 1);
                        break;
                    case I_RRMVXX:
                    case I_OPQ:
                    case I_PUSHQ:
                    case I_POPQ:
                        f.valP.set(F.pc.get() + 2);
                        break;
                    case I_CALL:
		    	switch (f.iFn.getValueProducedInt()) {				//6.2 set valP based on iFn
			    case 0x9:
                        	f.valP.set(F.pc.get() + 10);
				break;
			    default:
                        	f.valP.set(F.pc.get() + 9);
				break;
                   	}
                        break;
                    case I_JXX:
			f.valP.set(F.pc.get() + 9);
			break;
                    case I_IRMOVQ:
                    case I_RMMOVQ:
                    case I_MRMOVQ:
		    case I_IOPQ:							//6.1 valP
                        f.valP.set(F.pc.get() + 10);
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        } catch (AbstractMainMemory.InvalidAddressException iae) {
            f.stat.set(S_ADR);
        }
    }

    /**
     * The DECODE stage of CPU
     *
     * @throws Register.TimingException if a stage tries to access a stage register that was never written to.
     */

    @Override
    protected void decode() throws Register.TimingException {

        // pass-through signals
        d.stat.set(D.stat.getInt());
        d.iCd.set(D.iCd.getInt());
        d.iFn.set(D.iFn.getInt());
        d.valC.set(D.valC.get());
        d.valP.set(D.valP.get());

        if (D.stat.getInt() == S_AOK) {
            try {

                // srcA MUX
                switch (D.iCd.getInt()) {
                    case I_RRMVXX:
                    case I_RMMOVQ:
                    case I_OPQ:
                    case I_PUSHQ:
                        d.srcA.set(D.rA.getInt());
                        break;
                    case I_RET:
                    case I_POPQ:
                        d.srcA.set(R_RSP);
                        break;
                    default:
                        d.srcA.set(R_NONE);
                }

                // srcB MUX
                switch (D.iCd.getInt()) {
                    case I_RMMOVQ:
                    case I_MRMOVQ:
                    case I_OPQ:
		    case I_IOPQ:							//6.1 srcB = rB
                        d.srcB.set(D.rB.getInt());
                        break;
                    case I_CALL:
		    	switch (D.iFn.getInt()) {					//6.2 set srcB based on iFn
			    case 0x9:
                        	d.srcB.set(D.rB.getInt());
				break;
			    default:
                       		d.srcB.set(R_RSP);
				break;
                   	}
                        break;
                    case I_RET:
                    case I_PUSHQ:
                    case I_POPQ:
                        d.srcB.set(R_RSP);
                        break;
                    default:
                        d.srcB.set(R_NONE);
                }

                // dstE MUX
                switch (D.iCd.getInt()) {
                    case I_RRMVXX:
                    case I_IRMOVQ:
                    case I_OPQ:
		    case I_IOPQ:							//6.1 dstE = rB
                        d.dstE.set(D.rB.getInt());
                        break;
                    case I_CALL:
		    	switch (D.iFn.getInt()) {					//6.2 set dstE based on iFn
			    case 0x9:
                        	d.dstE.set(R_NONE);
				break;
			    default:
                       		d.dstE.set(R_RSP);
				break;
                   	}
			break;
                    case I_RET:
                    case I_PUSHQ:
                    case I_POPQ:
                        d.dstE.set(R_RSP);
                        break;
                    default:
                        d.dstE.set(R_NONE);
                }

                // dstM MUX
                switch (D.iCd.getInt()) {
                    case I_MRMOVQ:
                    case I_POPQ:
			d.dstM.set(D.rA.getInt());
			break;
		    case I_CALL:
		    	switch (D.iFn.getInt()) {					//6.2 set dstM based on iFn
			    case 0x9:
                        	d.dstM.set(D.rA.getInt());
				break;
			    default:
				d.dstM.set(R_NONE);
				break;
                   	}
                        break;
                    default:
                        d.dstM.set(R_NONE);
                }

                try {
                    // read valA from register file
                    if (d.srcA.getValueProducedInt() != R_NONE)
                        d.valA.set(reg.get(d.srcA.getValueProducedInt()));
                    else
                        d.valA.set(0);

                    // read valB from register file
                    if (d.srcB.getValueProducedInt() != R_NONE)
                        d.valB.set(reg.get(d.srcB.getValueProducedInt()));
                    else
                        d.valB.set(0);

                } catch (RegisterSet.InvalidRegisterNumberException irne) {
                    throw new InvalidInstructionException(irne);
                }

            } catch (InvalidInstructionException iie) {
                d.stat.set(S_INS);
            }
        }

        if (d.stat.getValueProducedInt() != S_AOK) {
            d.srcA.set(R_NONE);
            d.srcB.set(R_NONE);
            d.dstE.set(R_NONE);
            d.dstM.set(R_NONE);
        }
    }

    /**
     * The EXECUTE stage of CPU
     *
     * @throws Register.TimingException if a stage tries to access a stage register that was never written to.
     */

    @Override
    protected void execute() throws Register.TimingException {

        // pass-through signals
        e.stat.set(E.stat.getInt());
        e.iCd.set(E.iCd.getInt());
        e.iFn.set(E.iFn.getInt());
        e.valC.set(E.valC.get());
        e.valA.set(E.valA.get());
        e.dstE.set(E.dstE.getInt());
        e.dstM.set(E.dstM.getInt());
        e.valP.set(E.valP.get());

        if (E.stat.getInt() == S_AOK) {

            // aluA MUX
            long aluA;
            switch (E.iCd.getInt()) {
                case I_RRMVXX:
                case I_OPQ:
                    aluA = E.valA.get();
                    break;
                case I_IRMOVQ:
                case I_MRMOVQ:
                case I_RMMOVQ:
		case I_IOPQ:								//6.1 aluA = valC
                    aluA = E.valC.get();
                    break;
                case I_RET:
                case I_POPQ:
                    aluA = 8;
                    break;
                case I_CALL:
		    switch (E.iFn.getInt()) {						//6.2 set aluA based on iFn
			case 0x9:
			    aluA = E.valC.get();
			    break;
			default:
			    aluA = -8
			    break;
                    }
		    break;
                case I_PUSHQ:
                    aluA = -8;
                    break;
                default:
                    aluA = 0;
            }

            // aluB MUX
            long aluB;
            switch (E.iCd.getInt()) {
                case I_RRMVXX:
                case I_IRMOVQ:
                    aluB = 0;
                    break;
                case I_RMMOVQ:
                case I_MRMOVQ:
                case I_OPQ:
		case I_IOPQ:								//6.1 aluB = valB
                case I_CALL:								//6.2 aluB = valB same for both iFn
                case I_RET:
                case I_PUSHQ:
                case I_POPQ:
                    aluB = E.valB.get();
                    break;
                default:
                    aluB = 0;
            }

            // aluFun and setCC muxes MUX
            int aluFun;
            boolean setCC;
            switch (E.iCd.getInt()) {
                case I_RRMVXX:
                case I_IRMOVQ:
                case I_RMMOVQ:
                case I_MRMOVQ:
                case I_CALL:								//6.2 aluFun same for both iFn
                case I_RET:
                case I_PUSHQ:
                case I_POPQ:
                    aluFun = A_ADDQ;
                    setCC = false;
                    break;
                case I_OPQ:
		case I_IOPQ:								//6.1 aluFun
                    aluFun = E.iFn.getInt();
                    setCC = true;
                    break;
                default:
                    aluFun = 0;
                    setCC = false;
            }

            // the ALU
            boolean overflow;
            switch (aluFun) {
                case A_ADDQ:
                    e.valE.set(aluB + aluA);
                    overflow = ((aluB < 0) == (aluA < 0)) && ((e.valE.getValueProduced() < 0) != (aluB < 0));
                    break;
                case A_SUBQ:
                    e.valE.set(aluB - aluA);
                    overflow = ((aluB < 0) != (aluA < 0)) && ((e.valE.getValueProduced() < 0) != (aluB < 0));
                    break;
                case A_ANDQ:
                    e.valE.set(aluB & aluA);
                    overflow = false;
                    break;
                case A_XORQ:
                    e.valE.set(aluB ^ aluA);
                    overflow = false;
                    break;
                case A_MULQ:
                    long result = aluB * aluA;
                    e.valE.set(result);
                    overflow = aluB != 0 && result / aluB != aluA;
                    break;
                case A_DIVQ:
                    e.valE.set(aluA == 0 ? aluB : aluB / aluA);
                    overflow = aluA == 0;
                    break;
                case A_MODQ:
                    e.valE.set(aluA == 0 ? aluB : aluB % aluA);
                    overflow = aluA == 0;
                    break;
                default:
                    overflow = false;
            }

            // CC MUX
            if (setCC)
                p.cc.set(((e.valE.getValueProduced() == 0) ? 0x100 : 0) | ((e.valE.getValueProduced() < 0) ? 0x10 : 0) | (overflow ? 0x1 : 0));
            else
                p.cc.set(P.cc.getInt());

            // cnd MUX
            boolean cnd;
            switch (E.iCd.getInt()) {
                case I_JXX:
                case I_RRMVXX:
                    boolean zf = (P.cc.getInt() & 0x100) != 0;
                    boolean sf = (P.cc.getInt() & 0x010) != 0;
                    boolean of = (P.cc.getInt() & 0x001) != 0;
                    switch (E.iFn.getInt()) {
                        case C_NC:
                            cnd = true;
                            break;
                        case C_LE:
                            cnd = (sf ^ of) | zf;
                            break;
                        case C_L:
                            cnd = sf ^ of;
                            break;
                        case C_E:
                            cnd = zf;
                            break;
                        case C_NE:
                            cnd = !zf;
                            break;
                        case C_GE:
                            cnd = !(sf ^ of);
                            break;
                        case C_G:
                            cnd = !(sf ^ of) & !zf;
                            break;
                        default:
                            throw new AssertionError();
                    }
                    break;
                default:
                    cnd = true;
            }
            e.cnd.set(cnd ? 1 : 0);

        } else
            e.cnd.set(0);
    }

    /**
     * The MEMORY stage of CPU
     *
     * @throws Register.TimingException if a stage tries to access a stage register that was never written to.
     */

    @Override
    protected void memory() throws Register.TimingException {

        // pass-through signals
        m.iCd.set(M.iCd.getInt());
        m.iFn.set(M.iFn.getInt());
        m.cnd.set(M.cnd.getInt());
        m.valE.set(M.valE.get());
        m.dstE.set(M.dstE.getInt());
        m.dstM.set(M.dstM.getInt());
        m.valP.set(M.valP.get());

        if (M.stat.getInt() == S_AOK) {
            try {

                // write Main Memory
                switch (M.iCd.getInt()) {
                    case I_RMMOVQ:
                    case I_PUSHQ:
                        mem.writeLong(M.valE.get(), M.valA.get());
                        break;
                    case I_CALL:
		    	switch (M.iFn.getInt()) {					//6.2 write based on iFn
			    case 0x9:
				m.valM.set(M.valP.get());	
			        break;
			    default:
                        	mem.writeLong(M.valE.get(), M.valP.get());
			    break;
                        }
                        break;
                    default:
                }

                // valM MUX (read main memory)
                switch (M.iCd.getInt()) {
                    case I_MRMOVQ:
                        m.valM.set(mem.readLong(M.valE.get()));
                        break;
                    case I_RET:
                    case I_POPQ:
                        m.valM.set(mem.readLong(M.valA.get()));
                        break;
                    default:
                }
                m.stat.set(M.stat.getInt());

            } catch (AbstractMainMemory.InvalidAddressException iae) {
                m.stat.set(S_ADR);
            } catch (AbstractMainMemory.OutOfMemoryException oom) {
                m.stat.set(S_OOM);
            }

        } else {
            m.stat.set(M.stat.getInt());
        }
    }

    /**
     * The WRITE BACK stage of CPU
     *
     * @throws InvalidInstructionException                if instruction is invalid (including invalid register number).
     * @throws AbstractMainMemory.InvalidAddressException if instruction attemps an invalid memory access (either instruction or data).
     * @throws AbstractMainMemory.OutOfMemoryException    if too many 1K pages have been accessed by the program.
     * @throws MachineHaltException                       if instruction halts the CPU.
     * @throws Register.TimingException                   if a stage tries to access a stage register that was never written to.
     */
    @Override
    protected void writeBack() throws MachineHaltException, InvalidInstructionException, AbstractMainMemory.InvalidAddressException, AbstractMainMemory.OutOfMemoryException, Register.TimingException {
        if (W.stat.getInt() == S_AOK)
            try {
                try {

                    // write valE to register file
                    if (W.dstE.getInt() != R_NONE && W.cnd.getInt() == 1)
                        reg.set(W.dstE.getInt(), W.valE.get());

                    // write valM to register file
                    if (W.dstM.getInt() != R_NONE)
                        reg.set(W.dstM.getInt(), W.valM.get());

                    w.stat.set(W.stat.getInt());

                } catch (RegisterSet.InvalidRegisterNumberException irne) {
                    throw new InvalidInstructionException(irne);
                }

            } catch (InvalidInstructionException iie) {
                w.stat.set(S_INS);
            }
        else
            w.stat.set(W.stat.getInt());

        if (w.stat.getValueProducedInt() == S_ADR)
            throw new AbstractMainMemory.InvalidAddressException();
        else if (w.stat.getValueProducedInt() == S_OOM)
            throw new AbstractMainMemory.OutOfMemoryException();
        else if (w.stat.getValueProducedInt() == S_INS)
            throw new InvalidInstructionException();
        else if (w.stat.getValueProducedInt() == S_HLT)
            throw new MachineHaltException();

        // Compute newPC
        if (W.stat.getInt() == S_AOK)
            newPC();
    }

    /**
     * Pseudo-stage to compute the new PC value
     */
    private void newPC() {
        switch (E.iCd.getInt()) {
            case I_CALL:
		switch (M.iFn.getInt()) {					//6.2 find new PC based on iFn
		    case 0x9:
			w.pc.set(M.valE.get());
		    	break;
		    default:
                	w.pc.set(E.valC.get());
		    	break;
                }
                break;
            case I_JXX:
                if (M.cnd.getInt() == 1)
                    w.pc.set(E.valC.get());
                else
                    w.pc.set(E.valP.get());
                break;
            case I_RET:
                w.pc.set(W.valM.get());
                break;
            default:
                w.pc.set(E.valP.get());
        }
    }
}
