package cn.yyx.contentassist.codeutils;

import java.util.List;

import cn.yyx.contentassist.codepredict.CodeSynthesisException;
import cn.yyx.contentassist.codesynthesis.CSFlowLineQueue;
import cn.yyx.contentassist.codesynthesis.CodeSynthesisHelper;
import cn.yyx.contentassist.codesynthesis.data.CSFlowLineData;
import cn.yyx.contentassist.codesynthesis.flowline.FlowLineNode;
import cn.yyx.contentassist.codesynthesis.statementhandler.CSStatementHandler;
import cn.yyx.contentassist.commonutils.SimilarityHelper;

public class intersectionType extends type{
	
	List<type> tps = null;
	
	public intersectionType(List<type> tps) {
		this.tps = tps;
	}
	
	@Override
	public boolean CouldThoughtSame(OneCode t) {
		if (t instanceof intersectionType)
		{
			return SimilarityHelper.CouldThoughtListsOfTypeSame(tps, ((intersectionType) t).tps);
		}
		return false;
	}

	@Override
	public double Similarity(OneCode t) {
		if (t instanceof intersectionType)
		{
			return SimilarityHelper.ComputeListsOfTypeSimilarity(tps, ((intersectionType) t).tps);
		}
		return 0;
	}
	
	@Override
	public List<FlowLineNode<CSFlowLineData>> HandleCodeSynthesis(CSFlowLineQueue squeue, CSStatementHandler smthandler)
			throws CodeSynthesisException {
		return CodeSynthesisHelper.HandleMultipleConcateType(squeue, smthandler, tps, "&");
	}
	
}