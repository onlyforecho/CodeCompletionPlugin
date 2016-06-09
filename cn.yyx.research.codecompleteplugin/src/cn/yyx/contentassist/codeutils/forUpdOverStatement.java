package cn.yyx.contentassist.codeutils;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import cn.yyx.contentassist.codepredict.CodeSynthesisException;
import cn.yyx.contentassist.codesynthesis.CSFlowLineBackTraceGenerationHelper;
import cn.yyx.contentassist.codesynthesis.CSFlowLineHelper;
import cn.yyx.contentassist.codesynthesis.CSFlowLineQueue;
import cn.yyx.contentassist.codesynthesis.CodeSynthesisHelper;
import cn.yyx.contentassist.codesynthesis.data.CSFlowLineData;
import cn.yyx.contentassist.codesynthesis.data.CSForExpOverProperty;
import cn.yyx.contentassist.codesynthesis.data.CSForUpdOverProperty;
import cn.yyx.contentassist.codesynthesis.data.DataStructureSignalMetaInfo;
import cn.yyx.contentassist.codesynthesis.flowline.FlowLineNode;
import cn.yyx.contentassist.codesynthesis.statementhandler.CSStatementHandler;
import cn.yyx.contentassist.commonutils.BackSearchResult;
import cn.yyx.contentassist.commonutils.ListHelper;

public class forUpdOverStatement extends rawForUpdOverStatement implements SWrapper{
	
	statement smt = null;
	
	public forUpdOverStatement(statement smt, String smtcode) {
		super(smtcode);
		this.smt = smt;
	}

	@Override
	public boolean CouldThoughtSame(OneCode t) {
		if (t instanceof forUpdOverStatement)
		{
			return smt.CouldThoughtSame(((forUpdOverStatement)t).smt);
		}
		if (t instanceof rawForUpdOverStatement)
		{
			return true;
		}
		if (t instanceof statement)
		{
			return smt.CouldThoughtSame(t);
		}
		return false;
	}

	@Override
	public double Similarity(OneCode t) {
		if (t instanceof forUpdOverStatement)
		{
			return smt.Similarity(((forUpdOverStatement)t).smt);
		}
		if (t instanceof rawForUpdOverStatement)
		{
			return 0.5;
		}
		if (t instanceof statement)
		{
			return smt.Similarity(t);
		}
		return 0;
	}
	
	@Override
	public List<FlowLineNode<CSFlowLineData>> HandleCodeSynthesis(CSFlowLineQueue squeue, CSStatementHandler smthandler)
			throws CodeSynthesisException {
		Stack<Integer> signals = new Stack<Integer>();
		signals.push(DataStructureSignalMetaInfo.CommonForUpdWaitingOver);
		BackSearchResult br = squeue.BackSearchForTheNextOfSpecialClass(CSForExpOverProperty.class, signals);
		if (!br.isValid())
		{
			throw new CodeSynthesisException("no exp over before upd over?");
		}
		List<FlowLineNode<CSFlowLineData>> smtls = smt.HandleCodeSynthesis(squeue, smthandler);
		smtls = CSFlowLineHelper.ConcateOneFlowLineList(null, smtls, ") " + CodeSynthesisHelper.GenerateBlockCode(smthandler));
		if (smtls == null || smtls.size() == 0)
		{
			return null;
		}
		Iterator<FlowLineNode<CSFlowLineData>> ritr = smtls.iterator();
		while (ritr.hasNext())
		{
			FlowLineNode<CSFlowLineData> smtln = ritr.next();
			if (!br.isSelfisneeded())
			{
				CSFlowLineBackTraceGenerationHelper.GenerateNotYetAddedSynthesisCode(squeue, smthandler, smtln, br.getCnode());
			}
			List<FlowLineNode<CSFlowLineData>> rls = CSFlowLineBackTraceGenerationHelper.GenerateNotYetAddedSynthesisCode(squeue, smthandler, smtln, null);
			if (rls != null && rls.size() > 0)
			{
				smtln.setSynthesisdata(rls.get(0).getData());
			}
		}
		ListHelper.AddExtraPropertyToAllListNodes(smtls, new CSForUpdOverProperty(null));
		return smtls;
	}

	@Override
	public statement GetContent() {
		return smt;
	}
	
}