package cn.yyx.contentassist.codeutils;

import java.util.List;

import cn.yyx.contentassist.codepredict.CodeSynthesisException;
import cn.yyx.contentassist.codesynthesis.CSFlowLineQueue;
import cn.yyx.contentassist.codesynthesis.CodeSynthesisHelper;
import cn.yyx.contentassist.codesynthesis.data.CSFlowLineData;
import cn.yyx.contentassist.codesynthesis.flowline.FlowLineNode;
import cn.yyx.contentassist.codesynthesis.statementhandler.CSStatementHandler;

public class superClassMemberInvoke extends classInvoke{
	
	firstArgReferedExpression rexp = null; // warning: rexp could be null.
	
	public superClassMemberInvoke(firstArgReferedExpression rexp) {
		this.rexp = rexp;
	}

	/*@Override
	public boolean HandleCodeSynthesis(CodeSynthesisQueue squeue, Stack<TypeCheck> expected, SynthesisHandler handler,
			CSNode result, AdditionalInfo ai) {
		String mcode = ai.getMethodName();
		CSNode recs = new CSNode(CSNodeType.HalfFullExpression);
		if (rexp != null)
		{
			boolean conflict = rexp.HandleCodeSynthesis(squeue, expected, handler, recs, ai);
			if (conflict)
			{
				return true;
			}
		}
		CSNode spec = new CSNode(CSNodeType.HalfFullExpression);
		boolean conflict = CodeSynthesisHelper.HandleMethodSpecificationInfer(squeue, expected, handler, spec, ai, mcode);
		if (conflict)
		{
			return true;
		}
		result.SetCSNodeContent(CSNodeHelper.ConcatTwoNodes(recs, spec, ".super.", -1));
		// result.AddOneData(mcode, null);
		return false;
	}*/

	@Override
	public boolean CouldThoughtSame(OneCode t) {
		if (t instanceof superClassMemberInvoke)
		{
			return true;
		}
		return false;
	}

	@Override
	public double Similarity(OneCode t) {
		if (t instanceof superClassMemberInvoke)
		{
			return 1;
		}
		return 0;
	}

	@Override
	public List<FlowLineNode<CSFlowLineData>> HandleCodeSynthesis(CSFlowLineQueue squeue, CSStatementHandler smthandler)
			throws CodeSynthesisException {
		return CodeSynthesisHelper.HandleClassInvokeCodeSynthesis(squeue, smthandler, rexp, "super.");
	}
	
}