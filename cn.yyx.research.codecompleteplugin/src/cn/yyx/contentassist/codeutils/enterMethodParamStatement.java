package cn.yyx.contentassist.codeutils;

import java.util.LinkedList;
import java.util.List;

import cn.yyx.contentassist.codepredict.CodeSynthesisException;
import cn.yyx.contentassist.codesynthesis.CSFlowLineQueue;
import cn.yyx.contentassist.codesynthesis.CSStatementHandler;
import cn.yyx.contentassist.codesynthesis.data.CSEnterParamInfoData;
import cn.yyx.contentassist.codesynthesis.data.CSFlowLineData;
import cn.yyx.contentassist.codesynthesis.flowline.FlowLineNode;
import cn.yyx.contentassist.codesynthesis.flowline.FlowLineStack;

public class enterMethodParamStatement extends statement{
	
	int times = -1;
	
	public enterMethodParamStatement(int times) {
		this.times = times;
	}
	
	@Override
	public boolean CouldThoughtSame(OneCode t) {
		if (t instanceof enterMethodParamStatement)
		{
			return true;
		}
		return false;
	}

	@Override
	public double Similarity(OneCode t) {
		if (t instanceof enterMethodParamStatement)
		{
			return 1;
		}
		return 0;
	}
	
	/*@Override
	public boolean HandleCodeSynthesis(CodeSynthesisQueue squeue, Stack<TypeCheck> expected, SynthesisHandler handler,
			CSNode result, AdditionalInfo ai) {
		CSEnterParamInfoData cs = new CSEnterParamInfoData(times);
		squeue.add(cs);
		return false;
	}*/

	@Override
	public List<FlowLineNode<CSFlowLineData>> HandleCodeSynthesis(CSFlowLineQueue squeue, CSStatementHandler smthandler)
			throws CodeSynthesisException {
		List<FlowLineNode<CSFlowLineData>> result = new LinkedList<FlowLineNode<CSFlowLineData>>();
		result.add(new FlowLineNode<CSFlowLineData>(new CSEnterParamInfoData(times, squeue.GenerateNewNodeId(), smthandler.getSete(), "", null, false, true, null, null, squeue.GetLastHandler()), smthandler.getProb()));
		return result;
	}

	@Override
	public boolean HandleOverSignal(FlowLineStack cstack) throws CodeSynthesisException {
		return false;
	}
	
}