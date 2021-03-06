package cn.yyx.contentassist.codepredict;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;

import cn.yyx.contentassist.aerospikehandle.AeroLifeCycle;
import cn.yyx.contentassist.codecompletion.AeroMetaData;
import cn.yyx.contentassist.codecompletion.CodeCompletionMetaInfo;
import cn.yyx.contentassist.codecompletion.PredictMetaInfo;
import cn.yyx.contentassist.codesynthesis.flowline.CodeSynthesisFlowLines;
import cn.yyx.contentassist.codesynthesis.flowline.FlowLineHelper;
import cn.yyx.contentassist.codesynthesis.flowline.FlowLineNode;
import cn.yyx.contentassist.codesynthesis.flowline.PreTryFlowLineNode;
import cn.yyx.contentassist.codesynthesis.flowline.PreTryFlowLines;
import cn.yyx.contentassist.codeutils.statement;
import cn.yyx.contentassist.commonutils.ASTOffsetInfo;
import cn.yyx.contentassist.commonutils.ContextHandler;
import cn.yyx.contentassist.commonutils.ListHelper;
import cn.yyx.contentassist.commonutils.StatementsMIs;
import cn.yyx.contentassist.commonutils.SynthesisHandler;
import cn.yyx.contentassist.commonutils.TKey;
import cn.yyx.research.language.simplified.JDTManager.ScopeOffsetRefHandler;

public class PredictionFetch {
	
	public List<String> FetchPrediction(JavaContentAssistInvocationContext javacontext, ScopeOffsetRefHandler handler, List<String> analist, ArrayList<String> result, ASTOffsetInfo aoi)
	{
		AeroLifeCycle alc = AeroLifeCycle.GetInstance();
		alc.Initialize();
		
		LinkedList<Sentence> setelist = SentenceHelper.TranslateStringsToSentences(analist);
		final Class<?> lastkind = setelist.getLast().getSmt().getClass();
		StatementsMIs smtmis = SentenceHelper.TranslateSentencesToStatements(setelist);
		// PreTryFlowLines<Sentence> fls = new PreTryFlowLines<Sentence>();
		PreTryFlowLines<Sentence> fls = DoPreTrySequencePredict(alc, setelist, smtmis, lastkind);// fls, PredictMetaInfo.PreTryNeedSize, 
		fls.TrimOverTails(PredictMetaInfo.PreTryNeedSize);
		
		ContextHandler ch = new ContextHandler(javacontext);
		SynthesisHandler sh = new SynthesisHandler(handler, ch);
		List<CodeSynthesisFlowLines> csfll = null;
		if (CodeCompletionMetaInfo.SerialMode)
		{
			csfll = DoRealCodePredictAndSynthesisInSerial(sh, alc, fls, aoi);
		} else {
			csfll = DoRealCodePredictAndSynthesisInParallel(sh, alc, fls, aoi);
		}
		
		List<String> list = new LinkedList<String>();
		Iterator<CodeSynthesisFlowLines> itr = csfll.iterator();
		while (itr.hasNext())
		{
			CodeSynthesisFlowLines csfl = itr.next();
			list.addAll(0, csfl.GetSynthesisOverCode());
		}
		return list;
	}
	
	protected List<CodeSynthesisFlowLines> DoRealCodePredictAndSynthesisInParallel(SynthesisHandler sh, AeroLifeCycle alc, PreTryFlowLines<Sentence> fls, ASTOffsetInfo aoi) {
		List<CodeSynthesisFlowLines> csfll = new LinkedList<CodeSynthesisFlowLines>();
		List<CodeSynthesisPredictTask> csptl = new LinkedList<CodeSynthesisPredictTask>();
		List<PreTryFlowLineNode<Sentence>> ots = fls.getOvertails();
		Iterator<PreTryFlowLineNode<Sentence>> otsitr = ots.iterator();
		int alen = AeroMetaData.codengram.length;
		int aidx = 0;
		while (otsitr.hasNext())
		{
			PreTryFlowLineNode<Sentence> fln = otsitr.next();
			aidx++;
			if (aidx > alen)
			{
				break;
			}
			CodeSynthesisFlowLines csfl = new CodeSynthesisFlowLines();
			csptl.add(new CodeSynthesisPredictTask(fln, sh, alc, csfl, aoi, AeroMetaData.codengram[aidx-1]));
			csfll.add(csfl);
		}
		List<Thread> tl = new LinkedList<Thread>();
		Iterator<CodeSynthesisPredictTask> csptlitr = csptl.iterator();
		while (csptlitr.hasNext())
		{
			CodeSynthesisPredictTask cspt = csptlitr.next();
			Thread t = new Thread(cspt);
			tl.add(t);
			t.start();
		}
		Iterator<Thread> tlitr = tl.iterator();
		while (tlitr.hasNext())
		{
			Thread t = tlitr.next();
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return csfll;
	}
	
	/**
	 * just for test use.
	 * @param sh
	 * @param alc
	 * @param fls
	 * @param aoi
	 * @return
	 */
	protected List<CodeSynthesisFlowLines> DoRealCodePredictAndSynthesisInSerial(SynthesisHandler sh, AeroLifeCycle alc, PreTryFlowLines<Sentence> fls, ASTOffsetInfo aoi) {
		List<CodeSynthesisFlowLines> csfll = new LinkedList<CodeSynthesisFlowLines>();
		List<CodeSynthesisPredictTask> csptl = new LinkedList<CodeSynthesisPredictTask>();
		List<PreTryFlowLineNode<Sentence>> ots = fls.getOvertails();
		Iterator<PreTryFlowLineNode<Sentence>> otsitr = ots.iterator();
		int alen = AeroMetaData.codengram.length;
		int aidx = 0;
		while (otsitr.hasNext())
		{
			PreTryFlowLineNode<Sentence> fln = otsitr.next();
			aidx++;
			if (aidx > alen)
			{
				break;
			}
			CodeSynthesisFlowLines csfl = new CodeSynthesisFlowLines();
			csptl.add(new CodeSynthesisPredictTask(fln, sh, alc, csfl, aoi, AeroMetaData.codengram[aidx-1]));
			csfll.add(csfl);
		}
		Iterator<CodeSynthesisPredictTask> csptlitr = csptl.iterator();
		while (csptlitr.hasNext())
		{
			CodeSynthesisPredictTask cspt = csptlitr.next();
			cspt.run();
		}
		return csfll;
	}
	
	protected PreTryFlowLines<Sentence> DoPreTrySequencePredict(AeroLifeCycle alc, final List<Sentence> setelist,
			final StatementsMIs smtmis, final Class<?> lastkind) {
		List<PreTryFlowLineNode<Sentence>> ots = new LinkedList<PreTryFlowLineNode<Sentence>>();
		final List<statement> smtlist = smtmis.getSmts();
		int smtsize = smtlist.size();
		final List<statement> smilist = smtmis.getSmis();
		int trysize = (int)Math.ceil(PredictMetaInfo.NgramMaxSize/2.0);
		trysize = Math.min(trysize, smtsize);
		for (int i=0;i<trysize;i++)
		{
			PreTryFlowLines<Sentence> fls = new PreTryFlowLines<Sentence>();
			DoOnePreTrySequencePredict(alc, fls, setelist.subList(i, smtsize), smtlist.subList(i, smtsize), smilist, lastkind); // , ListHelper.ConcateJoin(setelist, 0, i)
			fls.TrimOverTails(PredictMetaInfo.PreTryNeedSize);
			List<PreTryFlowLineNode<Sentence>> ot = fls.getOvertails();
			UniqueAddOts(ots, ot);
			if (ots.size() >= PredictMetaInfo.FinalPreTryNeedSize)
			{
				break;
			}
		}
		return new PreTryFlowLines<Sentence>(ots);
	}
	
	protected void UniqueAddOts(List<PreTryFlowLineNode<Sentence>> ots, List<PreTryFlowLineNode<Sentence>> ot)
	{
		Iterator<PreTryFlowLineNode<Sentence>> oitr = ot.iterator();
		while (oitr.hasNext())
		{
			PreTryFlowLineNode<Sentence> fln = oitr.next();
			if (!ListHelper.WholeKeyContains(ots, fln))
			{
				ots.add(fln);
			}
		}
	}
	
	protected void DoOnePreTrySequencePredict(AeroLifeCycle alc, PreTryFlowLines<Sentence> fls, List<Sentence> setelist,
			List<statement> smtlist, final List<statement> smtmilist, final Class<?> lastkind) { // int needsize,
		boolean first = true;
		Iterator<Sentence> itr = setelist.iterator();
		while (itr.hasNext())
		{
			Sentence ons = itr.next();
			if (!first)
			{
				boolean hasnextgeneration = DoOneRoundPreTrySequencePredict(alc, fls, ons, setelist, smtlist, smtmilist, lastkind);
				if (!hasnextgeneration)
				{
					return;
				}
			}
			if (first)
			{
				PreTryFlowLineNode<Sentence> fln = fls.InitialSeed(ons);
				first = false;
				if (setelist.size() == 1)
				{
					fls.AddOverFlowLineNode(fln, null);
				}
			}
		}
		int size = fls.GetValidOveredSize();
		int turn = 0;
		while (turn < PredictMetaInfo.PreTryMaxExtendStep)
		{
			turn++;
			boolean hasnextgeneration = DoOneRoundPreTrySequencePredict(alc, fls, null, setelist, smtlist, smtmilist, lastkind);
			if (!hasnextgeneration)
			{
				return;
			}
			size = fls.GetValidOveredSize();
			if (size >= PredictMetaInfo.FinalPreTryNeedSize && turn >= PredictMetaInfo.PreTryMinExtendStep) {
				break;
			}
		}
	}
	
	protected boolean DoOneRoundPreTrySequencePredict(AeroLifeCycle alc, PreTryFlowLines<Sentence> fls, Sentence ons, List<Sentence> setelist,
			List<statement> smtlist, final List<statement> smtmilist, final Class<?> lastkind) { // int needsize,
		List<FlowLineNode<Sentence>> tails = fls.getTails();
		int tailsize = tails.size();
		int avgsize = (int)Math.ceil((tailsize*1.0)/(AeroMetaData.codengram.length*1.0));
		if (avgsize == 0)
		{
			avgsize = 1;
		}
		List<PreTryPredictTask> ptpts = new LinkedList<PreTryPredictTask>();
		int count = 0;
		int taskid = 0;
		List<TKey> keys = new LinkedList<TKey>();
		List<StatementsMIs> smtmises = new LinkedList<StatementsMIs>();
		Iterator<FlowLineNode<Sentence>> itr = tails.iterator();
		while (itr.hasNext())
		{
			PreTryFlowLineNode<Sentence> fln = (PreTryFlowLineNode<Sentence>) itr.next();
			
			StatementsMIs smtmis = FlowLineHelper.LastToFirstStatementQueueWithMethodInvocationExtracted(fln);
			smtmises.add(smtmis);
			
			List<Sentence> ls = FlowLineHelper.LastNeededSentenceQueue(PredictMetaInfo.NgramMaxSize-1, fln, null);
			TKey tkey = ListHelper.ConcatJoin(ls);
			keys.add(tkey);
			
			count++;
			if (count >= avgsize)
			{
				taskid++;
				PreTryPredictTask ptpt = new PreTryPredictTask(taskid, alc, keys, smtmises, fln, smtlist, smtmilist, ons);
				ptpts.add(ptpt);
				keys.clear();
				smtmises.clear();
				count = 0;
			}
		}
		
		Queue<PreTryFlowLineNode<Sentence>> pppqueue = new PriorityQueue<PreTryFlowLineNode<Sentence>>();
		
		if (CodeCompletionMetaInfo.SerialMode) {
			DoRoundTaskRunInSerial(ptpts, pppqueue);
		} else {
			DoRoundTaskRunInParallel(ptpts, pppqueue);
		}
		
		boolean hasnextgeneration = !pppqueue.isEmpty();
		fls.BeginOperation();
		int ndsize = (int)(PredictMetaInfo.PreTryTotalMaxParSize);
		while (ndsize > 0 && (!pppqueue.isEmpty()))
		{
			PreTryFlowLineNode<Sentence> nf = pppqueue.poll();
			boolean couldterminate = TerminationHelper.couldTerminate(nf.getData(), lastkind, nf.getParent().getLength()+1, smtlist.size());
			fls.AddToNextLevel(nf, nf.getParent());
			// nf.setWholekey(FlowLineHelper.GetWholeTraceKey(nf));
			ndsize--;
			if (couldterminate)
			{
				fls.AddOverFlowLineNode(nf, nf.getParent());
			}
		}
		fls.EndOperation();
		return hasnextgeneration;
	}
	
	protected void DoRoundTaskRunInSerial(List<PreTryPredictTask> ptpts, Queue<PreTryFlowLineNode<Sentence>> pppqueue)
	{
		Iterator<PreTryPredictTask> ptptitr = ptpts.iterator();
		while (ptptitr.hasNext())
		{
			PreTryPredictTask ptpt = ptptitr.next();
			ptpt.run();
		}
		
		ptptitr = ptpts.iterator();
		while (ptptitr.hasNext())
		{
			PreTryPredictTask ptpt = ptptitr.next();
			List<PreTryFlowLineNode<Sentence>> ls = ptpt.GetResultList();
			pppqueue.addAll(ls);
		}
	}
	
	protected void DoRoundTaskRunInParallel(List<PreTryPredictTask> ptpts, Queue<PreTryFlowLineNode<Sentence>> pppqueue)
	{
		List<Thread> llths = new LinkedList<Thread>();
		Iterator<PreTryPredictTask> ptptitr = ptpts.iterator();
		while (ptptitr.hasNext())
		{
			PreTryPredictTask ptpt = ptptitr.next();
			Thread nt = new Thread(ptpt);
			nt.start();
			llths.add(nt);
		}
		
		Iterator<Thread> llitr = llths.iterator();
		ptptitr = ptpts.iterator();
		while (llitr.hasNext())
		{
			Thread t = llitr.next();
			PreTryPredictTask ptpt = ptptitr.next();
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			List<PreTryFlowLineNode<Sentence>> ls = ptpt.GetResultList();
			pppqueue.addAll(ls);
		}
	}
	
}