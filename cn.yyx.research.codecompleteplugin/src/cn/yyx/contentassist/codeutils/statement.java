package cn.yyx.contentassist.codeutils;

import cn.yyx.contentassist.codepredict.CodeSynthesisException;
import cn.yyx.contentassist.codesynthesis.flowline.FlowLineStack;

public abstract class statement implements OneCode {
	
	public abstract boolean HandleOverSignal(FlowLineStack cstack) throws CodeSynthesisException;
	
}