package cn.yyx.contentassist.commonutils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import cn.yyx.contentassist.codeutils.identifier;

public class CodeSynthesisHelper {
	
	/*public static boolean HandleRawTextSynthesis(String text, CodeSynthesisQueue squeue, SynthesisHandler handler,
			StringBuilder result, AdditionalInfo ai)
	{
		if (result != null)
		{
			result.append(text);
		}
		else
		{
			ErrorUtil.ErrorAndStop("What the fuch the rawText put where?");
		}
		return false;
	}*/
	
	public static boolean HandleBreakContinueCodeSynthesis(identifier id, CodeSynthesisQueue squeue, Stack<TypeCheck> expected, SynthesisHandler handler,
			CSNode result, AdditionalInfo ai, String wheretp)
	{
		StringBuilder fin = new StringBuilder(wheretp);
		CSNode csn = new CSNode(CSNodeType.TempUsed);
		boolean conflict = id.HandleCodeSynthesis(squeue, expected, handler, csn, null);
		if (conflict)
		{
			return true;
		}
		fin.append(wheretp + " " + csn.GetFirstDataWithoutTypeCheck());
		CSNode cs = new CSNode(CSNodeType.WholeStatement);
		cs.AddPossibleCandidates(fin.toString(), null);
		squeue.add(cs);
		return false;
	}
	
	public static String GenerateDimens(int count)
	{
		StringBuilder sb = new StringBuilder("");
		for (int i=0;i<count;i++)
		{
			sb.append("[]");
		}
		return sb.toString();
	}
	
	public static void HandleVarRefCodeSynthesis(Map<String, String> po, CodeSynthesisQueue squeue, Stack<TypeCheck> expected, SynthesisHandler handler,
			CSNode result, AdditionalInfo ai)
	{
		if (ai != null && ai.getDirectlyMemberHint() != null)
		{
			String hint = ai.getDirectlyMemberHint();
			RefAndModifiedMember ramm = TypeCheckHelper.GetMostLikelyRef(handler.getContextHandler(), po, hint, ai.isDirectlyMemberIsMethod());
			String ref = ramm.getRef();
			String member = ramm.getMember();
			String membertype = ramm.getMembertype();
			Class<?> c = TypeResolver.ResolveType(membertype);
			TypeCheck tc = new TypeCheck();
			tc.setExpreturntype(membertype);
			tc.setExpreturntypeclass(c);
			result.AddOneData(ref + "." + member, tc);
		}
		else
		{
			Set<String> codes = po.keySet();
			Iterator<String> citr = codes.iterator();
			while (citr.hasNext())
			{
				String code = citr.next();
				String type = po.get(code);
				Class<?> c = TypeResolver.ResolveType(type);
				TypeCheck tc = new TypeCheck();
				tc.setExpreturntype(type);
				tc.setExpreturntypeclass(c);
				result.AddOneData(code, tc);
			}
		}
	}
	
}