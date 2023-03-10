package org.canvacord;

import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceLoader;
import org.canvacord.persist.CacheManager;

import java.util.Optional;

public class TestCache {

	public static void main(String[] args) {

		Optional<Instance> testInstanceOpt = InstanceLoader.loadInstance("32202-1016848330992656415");
		if (testInstanceOpt.isEmpty()) throw new RuntimeException("explosion sound effect");

		Instance testInstance = testInstanceOpt.get();
		CacheManager.createInstanceData(testInstance);

	}

}
