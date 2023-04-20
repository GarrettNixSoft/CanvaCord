package org.canvacord;

import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceLoader;
import org.canvacord.instance.InstanceManager;
import org.canvacord.persist.CacheManager;

import java.util.Optional;

public class TestCache {

	public static void main(String[] args) {

		InstanceManager.loadInstances();
		Optional<Instance> testInstanceOpt = InstanceManager.getInstanceByCourseID("32202");
		if (testInstanceOpt.isEmpty()) throw new RuntimeException("explosion sound effect");

		Instance testInstance = testInstanceOpt.get();
		CacheManager.createInstanceData(testInstance);

	}

}
