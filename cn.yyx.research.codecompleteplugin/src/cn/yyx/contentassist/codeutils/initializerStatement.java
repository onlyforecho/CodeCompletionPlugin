package cn.yyx.contentassist.codeutils;

import java.util.Stack;

import cn.yyx.contentassist.commonutils.AdditionalInfo;
import cn.yyx.contentassist.commonutils.CSNode;
import cn.yyx.contentassist.commonutils.CodeSynthesisQueue;
import cn.yyx.contentassist.commonutils.SynthesisHandler;
import cn.yyx.contentassist.commonutils.TypeCheck;

public class initializerStatement extends statement{

	@Override
	public boolean CouldThoughtSame(OneCode t) {
		if (t instanceof initializerStatement)
		{
			return true;
		}
		return false;
	}

	@Override
	public double Similarity(OneCode t) {
		if (t instanceof initializerStatement)
		{
			return 1;
		}
		return 0;
	}

	@Override
	public boolean HandleOverSignal(Stack<Integer> cstack) {
		return false;
	}

	@Override
	public boolean HandleCodeSynthesis(CodeSynthesisQueue squeue, Stack<TypeCheck> expected, SynthesisHandler handler,
			CSNode result, AdditionalInfo ai) {
		return false;
	}
	
}