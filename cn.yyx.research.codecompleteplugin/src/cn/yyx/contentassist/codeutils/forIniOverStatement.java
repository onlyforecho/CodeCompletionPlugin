package cn.yyx.contentassist.codeutils;

import java.util.Stack;

import cn.yyx.contentassist.commonutils.AdditionalInfo;
import cn.yyx.contentassist.commonutils.CSNode;
import cn.yyx.contentassist.commonutils.CSNodeType;
import cn.yyx.contentassist.commonutils.CodeSynthesisQueue;
import cn.yyx.contentassist.commonutils.StructureSignalMetaInfo;
import cn.yyx.contentassist.commonutils.SynthesisHandler;
import cn.yyx.contentassist.commonutils.TypeCheck;

public class forIniOverStatement extends statement{

	@Override
	public boolean CouldThoughtSame(OneCode t) {
		if (t instanceof forIniOverStatement)
		{
			return true;
		}
		return false;
	}

	@Override
	public double Similarity(OneCode t) {
		if (t instanceof forIniOverStatement)
		{
			return 1;
		}
		return 0;
	}

	@Override
	public boolean HandleOverSignal(Stack<Integer> cstack) {
		int sttop = cstack.peek();
		if (sttop == StructureSignalMetaInfo.CommonForKindWaitingOver)
		{
			cstack.pop();
		}
		else
		{
			return true;
		}
		cstack.push(StructureSignalMetaInfo.CommonForInitWaitingOver);
		return false;
	}
	
	@Override
	public boolean HandleCodeSynthesis(CodeSynthesisQueue squeue, Stack<TypeCheck> expected, SynthesisHandler handler,
			CSNode result, AdditionalInfo ai) {
		CSNode cs = new CSNode(CSNodeType.SymbolMark);
		cs.AddOneData(";", null);
		squeue.add(cs);
		return false;
	}
	
}