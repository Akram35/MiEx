/*
 * BSD 3-Clause License
 * 
 * Copyright (c) 2024, Bram Stout Productions
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.bramstout.mcworldexporter.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomeRegistry {

	private static List<Biome> registeredBiomes = new ArrayList<Biome>();
	private static Map<String, Integer> nameToId = new HashMap<String, Integer>();
	private static Object mutex = new Object();
	
	public static int getIdForName(String name) {
		if(!name.contains(":"))
			name = "minecraft:" + name;
		Integer id = nameToId.get(name);
		if(id == null) {
			synchronized(mutex) {
				id = nameToId.get(name);
				if(id == null) {
					id = registeredBiomes.size();
					registeredBiomes.add(getBiomeFromName(name, id.intValue()));
					nameToId.put(name, id);
				}
			}
		}
		return id.intValue();
	}
	
	private static Biome getBiomeFromName(String name, int id) {
		return new Biome(name, id);
	}

	public static void recalculateTints() {
		for(Biome biome : registeredBiomes)
			biome.calculateTints();
	}
	
	public static Biome getBiome(int id) {
		return registeredBiomes.get(id);
	}
	
}
