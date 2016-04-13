package cn.yyx.contentassist.codesynthesis.flowline;

import java.util.Stack;

import cn.yyx.contentassist.codepredict.CodeSynthesisException;
import cn.yyx.contentassist.codesynthesis.data.CSFlowLineData;

public class FlowLineStack {
	
	FlowLineNode<CSFlowLineData> last = null;
	
	public FlowLineStack(FlowLineNode<CSFlowLineData> last) {
		this.last = last;
	}
	
	/*public void SetLastStructureSignal(int structuresignal)
	{
		last.getData().setStructsignal(structuresignal);
	}

	public FlowLineNode<CSFlowLineData> BackSearchForStructureSignal(int signal) {
		FlowLineNode<CSFlowLineData> tmp = last;
		while (tmp.HasPrev())
		{
			Integer sig = tmp.getData().getStructsignal();
			if ((sig != null) && (sig == signal))
			{
				return tmp;
			}
			tmp = tmp.getPrev();
		}
		return null;
	}*/
	
	public void EnsureAllSignalNull(FlowLineNode<CSFlowLineData> fromwhere) throws CodeSynthesisException {
		Stack<Integer> signals = new Stack<Integer>();
		FlowLineNode<CSFlowLineData> tmp = fromwhere;
		while (tmp != null)
		{
			tmp.getData().HandleStackSignal(signals);
			tmp = tmp.getPrev();
		}
		if (!signals.isEmpty())
		{
			throw new CodeSynthesisException("Has signal on stack, it should have no signals.");
		}
	}
	
	public FlowLineNode<CSFlowLineData> BackSearchForSpecialClass(Class<?> cls) {
		FlowLineNode<CSFlowLineData> tmp = last;
		while (tmp != null)
		{
			CSFlowLineData tmpdata = tmp.getData();
			if (tmpdata.getClass().equals(cls))
			{
				return tmp;
			}
		}
		return null;
	}
	
	public FlowLineNode<CSFlowLineData> GetSearchedAndHandledBlockStart() {
		return last.getData().getSynthesisCodeManager().getBlockstart();
	}
	
	public void EnsureAllSignalNull() throws CodeSynthesisException
	{
		EnsureAllSignalNull(last);
	}
	
	public void EnsureAllSignalNullFromSecondLast() throws CodeSynthesisException {
		EnsureAllSignalNull(last.getPrev());
	}
	
}