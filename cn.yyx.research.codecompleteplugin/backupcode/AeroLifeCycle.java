package cn.yyx.research.aerospikehandle;

import java.util.List;

public class AeroLifeCycle {

	public static final int code1sim = 1;

	public static final int codengram = 2;
	
	public static final String serverip = "192.168.1.101";
	
	boolean hasInitialized = false;

	public void Initialize() {
		Parameters param = new Parameters(serverip, 3000, null, null, "yyx", "code1sim");
		AeroHelper.ANewClient(code1sim, param);
		Parameters param2 = new Parameters(serverip, 3000, null, null, "yyx", "codengram");
		AeroHelper.ANewClient(codengram, param2);
		hasInitialized = true;
	}

	public void Destroy() {
		AeroHelper.CloseClient(1);
		AeroHelper.CloseClient(2);
		hasInitialized = false;
	}

	// must invoke in the environment of AeroLifeCycle.
	public List<PredictProbPair> AeroModelPredict(String key, int neededSize) {
		CheckInitialized();
		List<PredictProbPair> result = AeroHelper.GetNGramInAero(AeroLifeCycle.codengram, key, neededSize, null);
		// result.sort(new ProbPredictComparator());
		// int realsize = result.size();
		// if (realsize > neededSize)
		// {
		//	result = result.subList(0, neededSize);
		//	realsize = neededSize;
		// }
		return result;
	}
	
	/*public List<PredictProbPair> AeroModelPredict(String key, int neededSize, String oraclePredict) {
		CheckInitialized();
		List<PredictProbPair> result = AeroHelper.GetNGramInAero(AeroLifeCycle.codengram, key, neededSize, null);
		result.sort(new ProbPredictComparator());
		int realsize = result.size();
		if (realsize > neededSize)
		{
			result = result.subList(0, neededSize);
			realsize = neededSize;
		}
		int idx = result.indexOf(oraclePredict);
		if (idx == -1) {
			result.set(realsize - 1, new PredictProbPair(oraclePredict, PredictMetaInfo.NotExistProbability));
		}
		else
		{
			PredictProbPair obj = result.get(realsize - 1);
			PredictProbPair specified = result.get(idx);
			result.set(idx, obj);
			result.set(realsize - 1, specified);
		}
		return result;
	}*/
	
	private void CheckInitialized()
	{
		if (!hasInitialized)
		{
			System.err.println("Not Initialized. the system will exit.");
			System.exit(1);
		}
	}
	
}