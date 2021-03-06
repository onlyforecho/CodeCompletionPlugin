package cn.yyx.contentassist.codeutils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cn.yyx.contentassist.codepredict.CodeSynthesisException;
import cn.yyx.contentassist.codesynthesis.CSFlowLineHelper;
import cn.yyx.contentassist.codesynthesis.CSFlowLineQueue;
import cn.yyx.contentassist.codesynthesis.VirtualCSFlowLineQueue;
import cn.yyx.contentassist.codesynthesis.data.CSFlowLineData;
import cn.yyx.contentassist.codesynthesis.flowline.FlowLineNode;
import cn.yyx.contentassist.codesynthesis.flowline.FlowLineStack;
import cn.yyx.contentassist.codesynthesis.statementhandler.CSStatementHandler;
import cn.yyx.contentassist.codesynthesis.typeutil.CCType;
import cn.yyx.contentassist.codesynthesis.typeutil.TypeCheckHelper;
import cn.yyx.contentassist.codesynthesis.typeutil.TypeResolver;
import cn.yyx.contentassist.commonutils.ASTOffsetInfo;

public class returnStatement extends statement{
	
	referedExpression rexp = null; // warning: rexp could be null.
	
	public returnStatement(String smtcode, referedExpression rexp) {
		super(smtcode);
		this.rexp = rexp;
	}

	@Override
	public boolean CouldThoughtSame(OneCode t) {
		if (t instanceof returnStatement)
		{
			return true;
		}
		return false;
	}

	@Override
	public double Similarity(OneCode t) {
		if (t instanceof returnStatement)
		{
			double prob = 1;
			if ((rexp == null && ((returnStatement)t).rexp != null) || (rexp != null && ((returnStatement)t).rexp == null))
			{
				prob = 0.7;
			}
			if (rexp != null && ((returnStatement)t).rexp != null)
			{
				prob = rexp.Similarity(((returnStatement) t).rexp);
			}
			return 0.4 + 0.6*(prob);
		}
		return 0;
	}
	
	@Override
	public List<FlowLineNode<CSFlowLineData>> HandleCodeSynthesis(CSFlowLineQueue squeue, CSStatementHandler smthandler)
			throws CodeSynthesisException {
		List<FlowLineNode<CSFlowLineData>> result = null;
		if (rexp != null)
		{
			List<FlowLineNode<CSFlowLineData>> rels = rexp.HandleCodeSynthesis(squeue, smthandler);
			result = CSFlowLineHelper.ConcateOneFlowLineList("return ", rels, null);
		}
		else
		{
			result = new LinkedList<FlowLineNode<CSFlowLineData>>();
			result.add(new FlowLineNode<CSFlowLineData>(new CSFlowLineData(squeue.GenerateNewNodeId(), smthandler.getSete(), "return", null, null, squeue.GetLastHandler()), smthandler.getProb()));
			// return result;
		}
		return SpecialCheckAndFilterReturnStatement(squeue, smthandler, result);
	}
	
	private List<FlowLineNode<CSFlowLineData>> SpecialCheckAndFilterReturnStatement(CSFlowLineQueue squeue, CSStatementHandler smthandler, List<FlowLineNode<CSFlowLineData>> cstmp) throws CodeSynthesisException
	{
		ASTOffsetInfo aoi = smthandler.getAoi();
		String rtcode = aoi.getMethodDeclarationReturnType();
		if (rtcode != null && !aoi.isMethodReturnResolved())
		{
			LinkedList<CCType> rtls = TypeResolver.ResolveType(rtcode, squeue, smthandler);
			aoi.setMethodReturnResolvedType(rtls);
			aoi.setMethodReturnResolved(true);
		}
		LinkedList<CCType> rtrs = aoi.getMethodReturnResolvedType();
		List<FlowLineNode<CSFlowLineData>> result = new LinkedList<FlowLineNode<CSFlowLineData>>();
		if (cstmp != null)
		{
			Iterator<FlowLineNode<CSFlowLineData>> itr = cstmp.iterator();
			while (itr.hasNext())
			{
				FlowLineNode<CSFlowLineData> fln = itr.next();
				FlowLineNode<CSFlowLineData> bs = fln.getData().getSynthesisCodeManager().getBlockstart();
				if (bs == null && !(squeue instanceof VirtualCSFlowLineQueue))
				{
					// throw new CodeSynthesisException("return statement has no blockstart but there is something before it?");
					continue;
				}
				if (bs != null && bs.getPrev() != null)
				{
					continue;
					// throw new CodeSynthesisException("return statement has blockstart but there is something before blockstart?");
				}
				CCType flndcls = fln.getData().getDcls();
				if (TypeCheckHelper.CanBeMutualCast(rtrs, flndcls))
				{
					result.add(fln);
				}
			}
		}
		return result;
	}

	@Override
	public boolean HandleOverSignal(FlowLineStack cstack) throws CodeSynthesisException {
		cstack.EnsureAllSignalNull();
		return true;
	}
	
}