/*
 *  ========================================================================
 *  DIScrete event baSed Energy Consumption simulaTor 
 *    					             for Clouds and Federations (DISSECT-CF)
 *  ========================================================================
 *  
 *  This file is part of DISSECT-CF.
 *  
 *  DISSECT-CF is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or (at
 *  your option) any later version.
 *  
 *  DISSECT-CF is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 *  General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with DISSECT-CF.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  (C) Copyright 2014, Gabor Kecskemeti (gkecskem@dps.uibk.ac.at,
 *   									  kecskemeti.gabor@sztaki.mta.hu)
 */

package hu.mta.sztaki.lpds.cloud.simulator.iaas;

/**
 * This class is intentionally non mutable, allowing those users (e.g. the class
 * of IaaSService or Scheduler) who would want to share their current capacities
 * not to create a new instance for every new query.
 * 
 * @author 
 *         "Gabor Kecskemeti, Distributed and Parallel Systems Group, University of Innsbruck (c) 2013"
 *         "Gabor Kecskemeti, Laboratory of Parallel and Distributed Systems, MTA SZTAKI (c) 2012"
 */
public class ResourceConstraints implements Comparable<ResourceConstraints> {
	public final double requiredCPUs;
	public final double requiredProcessingPower;
	public final boolean requiredProcessingIsMinimum;
	public final long requiredMemory;
	public final double totalProcessingPower;
	public static final ResourceConstraints noResources = new ResourceConstraints(
			0, 0, 0);

	public ResourceConstraints(final double cpu, final double processing,
			final long memory) {
		this(cpu, processing, false, memory);
	}

	public ResourceConstraints(final double cpu, final double processing,
			boolean isMinimum, final long memory) {
		requiredCPUs = cpu;
		requiredMemory = memory;
		requiredProcessingPower = processing;
		totalProcessingPower = cpu * processing;
		requiredProcessingIsMinimum = isMinimum;
	}

	@Override
	public String toString() {
		return "ResourceConstraints(C:" + requiredCPUs + " P:"
				+ requiredProcessingPower + " M:" + requiredMemory + ")";
	}

	public ResourceConstraints multiply(final double times) {
		return times == 1 ? this : new ResourceConstraints(
				requiredCPUs * times, requiredProcessingPower,
				(long) (requiredMemory * times));

	}

	public static ResourceConstraints add(final ResourceConstraints... toAdd) {
		double tcpu = 0;
		double tpp = 0;
		long tm = 0;
		for (int i = 0; i < toAdd.length; i++) {
			tcpu += toAdd[i].requiredCPUs;
			tpp = Math.max(toAdd[i].requiredProcessingPower, tpp);
			tm += toAdd[i].requiredMemory;
		}
		tpp = tcpu == 0 ? 0 : tpp;
		return new ResourceConstraints(tcpu, tpp, tm);
	}

	public static ResourceConstraints subtract(final ResourceConstraints from,
			final ResourceConstraints what) {
		final double tcpu = from.requiredCPUs - what.requiredCPUs;
		return new ResourceConstraints(tcpu, tcpu == 0 ? 0 : Math.min(
				from.requiredProcessingPower, what.requiredProcessingPower),
				from.requiredMemory - what.requiredMemory);
	}

	public static ResourceConstraints negative(final ResourceConstraints rc) {
		return new ResourceConstraints(-rc.requiredCPUs,
				-rc.requiredProcessingPower, -rc.requiredMemory);
	}

	@Override
	public int compareTo(ResourceConstraints o) {
		return requiredCPUs == o.requiredCPUs
				&& requiredMemory == o.requiredMemory
				&& requiredProcessingPower == o.requiredProcessingPower ? 0
				: (requiredCPUs <= o.requiredCPUs
						&& requiredMemory <= o.requiredMemory
						&& requiredProcessingPower <= o.requiredProcessingPower ? -1
						: 1);
	}
}
