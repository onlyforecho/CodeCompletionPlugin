package cn.yyx.contentassist.codeutils;

import java.util.LinkedList;
import java.util.List;

import cn.yyx.contentassist.codepredict.CodeSynthesisException;
import cn.yyx.contentassist.codesynthesis.CSFlowLineQueue;
import cn.yyx.contentassist.codesynthesis.ErrorCheck;
import cn.yyx.contentassist.codesynthesis.data.CSFlowLineData;
import cn.yyx.contentassist.codesynthesis.flowline.FlowLineNode;
import cn.yyx.contentassist.codesynthesis.statementhandler.CSFieldAccessStatementHandler;
import cn.yyx.contentassist.codesynthesis.statementhandler.CSMethodReferenceStatementHandler;
import cn.yyx.contentassist.codesynthesis.statementhandler.CSStatementHandler;

public class codeHole extends referedExpression{
	
	public codeHole() {
	}
	
	@Override
	public boolean CouldThoughtSame(OneCode t) {
		if (t instanceof codeHole)
		{
			return true;
		}
		return false;
	}

	@Override
	public double Similarity(OneCode t) {
		if (t instanceof codeHole)
		{
			return 1;
		}
		return 0;
	}

	/*@Override
	public boolean HandleCodeSynthesis(CodeSynthesisQueue squeue, Stack<TypeCheck> expected, SynthesisHandler handler,
			CSNode result, AdditionalInfo ai) {
		result.setUsed(false);
		squeue.getLast().setHashole(true);
		return false;
	}*/

	@Override
	public List<FlowLineNode<CSFlowLineData>> HandleCodeSynthesis(CSFlowLineQueue squeue, CSStatementHandler smthandler)
			throws CodeSynthesisException {
		List<FlowLineNode<CSFlowLineData>> result = new LinkedList<FlowLineNode<CSFlowLineData>>();
		result.add(new FlowLineNode<CSFlowLineData>(new CSFlowLineData(squeue.GenerateNewNodeId(), smthandler.getSete(), "", null, true, true, null, null, squeue.GetLastHandler()), smthandler.getProb()));
		return result;
	}

	@Override
	public List<FlowLineNode<CSFlowLineData>> HandleInferredField(CSFlowLineQueue squeue,
			CSFieldAccessStatementHandler smthandler, String reservedword,
			List<FlowLineNode<CSFlowLineData>> expectedinfer) {
		ErrorCheck.NoGenerationCheck("codeHole should handle InferredField?");
		return null;
	}

	@Override
	public List<FlowLineNode<CSFlowLineData>> HandleInferredMethodReference(CSFlowLineQueue squeue,
			CSMethodReferenceStatementHandler smthandler, String reservedword,
			List<FlowLineNode<CSFlowLineData>> expectedinfer) {
		ErrorCheck.NoGenerationCheck("codeHole should handle InferredMethodReference?");
		return null;
	}
	
}