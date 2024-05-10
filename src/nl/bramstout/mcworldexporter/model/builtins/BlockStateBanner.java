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

package nl.bramstout.mcworldexporter.model.builtins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.bramstout.mcworldexporter.model.BakedBlockState;
import nl.bramstout.mcworldexporter.model.BlockState;
import nl.bramstout.mcworldexporter.model.Model;
import nl.bramstout.mcworldexporter.nbt.NBT_Tag;
import nl.bramstout.mcworldexporter.nbt.TAG_Compound;
import nl.bramstout.mcworldexporter.nbt.TAG_Int;
import nl.bramstout.mcworldexporter.nbt.TAG_List;
import nl.bramstout.mcworldexporter.nbt.TAG_String;

public class BlockStateBanner extends BlockState{

	public BlockStateBanner(String name) {
		super(name, null);
	}
	
	public String getDefaultTexture() {
		return "minecraft:entity/banner_base";
	}
	
	@Override
	public BakedBlockState getBakedBlockState(TAG_Compound properties, int x, int y, int z) {
		List<List<Model>> models = new ArrayList<List<Model>>();
		
		List<Model> list = new ArrayList<Model>();
		Model modelBase = new Model("banner_base", null, false);
		list.add(modelBase);
		models.add(list);
		Model modelBanner = new Model("banner", null, false);
		models.add(Arrays.asList(modelBanner));
		
		boolean isWall = name.contains("wall");
		float rotY = 0f;
		if (isWall) {
			String val = properties.getElement("facing").asString();
			if (val == null)
				val = "north";
			if (val.equals("north")) {
				rotY = 180f;
			} else if (val.equals("east")) {
				rotY = 270f;
			} else if (val.equals("south")) {
				rotY = 0f;
			} else if (val.equals("west")) {
				rotY = 90f;
			}
		} else {
			String val = properties.getElement("rotation").asString();
			if (val == null)
				val = "0";
			int ival = 0;
			try {
				ival = Integer.parseInt(val);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			rotY = (((float) ival) / 16f) * 360f;
		}
		
		modelBase.addTexture("base", "minecraft:entity/banner_base");
		
		float scale = 2f / 3f;
		float offsetX = 8f;
		float offsetY = isWall ? 13f : 28.666666f;
		float offsetZ = isWall ? 1f : 8f;
		
		float minX = -10f * scale + offsetX;
		float minY = -1f * scale + offsetY;
		float minZ = -1f * scale + offsetZ;
		float maxX = 10f * scale + offsetX;
		float maxY = 1f * scale + offsetY;
		float maxZ = 1f * scale + offsetZ;
		
		modelBase.addEntityCube(new float[] { minX, minY, minZ, maxX, maxY, maxZ }, new float[] { 0f, 10.5f, 11f, 11.5f }, "#base");

		// Post part
		if (!isWall) {
			minX = -1f * scale + offsetX;
			minY = -43f * scale + offsetY;
			minZ = -1f * scale + offsetZ;
			maxX = 1f * scale + offsetX;
			maxY = -1f * scale + offsetY;
			maxZ = 1f * scale + offsetZ;
			
			modelBase.addEntityCube(new float[] { minX, minY, minZ, maxX, maxY, maxZ }, new float[] { 11f, 0f, 13f, 11f }, "#base");
		}
		
		modelBase.rotate(0, rotY, false);
		
		String extraData = getExtraData(properties);
		modelBanner.addTexture("texture", "banner:banner_" + Integer.toHexString(extraData.hashCode()));
		modelBanner.setExtraData(extraData);
		
		minX = -10f * scale + offsetX;
		minY = -39f * scale + offsetY;
		minZ = 1f * scale + offsetZ;
		maxX = 10f * scale + offsetX;
		maxY = 1f * scale + offsetY;
		maxZ = 2f * scale + offsetZ;
		
		modelBanner.addEntityCube(new float[] { minX, minY, minZ, maxX, maxY, maxZ }, new float[] { 0f, 0f, 10.5f, 10.25f }, "#texture");
		
		modelBanner.rotate(0, rotY, false);
		
		return new BakedBlockState(name, models, transparentOcclusion, leavesOcclusion, detailedOcclusion, 
				individualBlocks, hasLiquid(properties), caveBlock, false, false, false, false, false, false, false, false, true, 0, null);
	}
	
	private String getExtraData(TAG_Compound properties) {
		String extraData = "{ ";

		extraData = extraData + "\"color\": " + getBannerColor() + ", ";

		extraData = extraData + "\"patterns\": [";
		NBT_Tag patternsTag = properties.getElement("Patterns");
		if(patternsTag == null)
			patternsTag = properties.getElement("patterns");
		if (patternsTag != null) {
			int i = 0;
			for (NBT_Tag patternNBTTag : ((TAG_List) patternsTag).elements) {
				TAG_Compound patternTag = (TAG_Compound) patternNBTTag;
				NBT_Tag patternNameTag = patternTag.getElement("Pattern");
				if(patternNameTag == null)
					patternNameTag = patternTag.getElement("pattern");
				String patternName = ((TAG_String) patternNameTag).value;
				NBT_Tag patternColorTag = patternTag.getElement("Color");
				String colorString = "0";
				if(patternColorTag != null)
					colorString = Integer.toString(((TAG_Int) patternTag.getElement("Color")).value);
				else {
					patternColorTag = patternTag.getElement("color");
					if(patternColorTag != null) {
						colorString = "\"" + ((TAG_String) patternColorTag).value + "\"";
					}
				}

				extraData = extraData + " { \"name\": \"" + patternName + "\", \"color\": " + colorString
						+ (i >= (((TAG_List) patternsTag).elements.length - 1) ? "} " : "}, ");
				++i;
			}
		}

		return extraData + "] }";
	}

	private int getBannerColor() {
		String colorString = name.replace("minecraft:", "").replace("_wall_banner", "").replace("_banner", "");

		if (colorString.equals("white")) {
			return 0;
		} else if (colorString.equals("orange")) {
			return 1;
		} else if (colorString.equals("magenta")) {
			return 2;
		} else if (colorString.equals("light_blue")) {
			return 3;
		} else if (colorString.equals("yellow")) {
			return 4;
		} else if (colorString.equals("lime")) {
			return 5;
		} else if (colorString.equals("pink")) {
			return 6;
		} else if (colorString.equals("gray")) {
			return 7;
		} else if (colorString.equals("light_gray")) {
			return 8;
		} else if (colorString.equals("cyan")) {
			return 9;
		} else if (colorString.equals("purple")) {
			return 10;
		} else if (colorString.equals("blue")) {
			return 11;
		} else if (colorString.equals("brown")) {
			return 12;
		} else if (colorString.equals("green")) {
			return 13;
		} else if (colorString.equals("red")) {
			return 14;
		} else if (colorString.equals("black")) {
			return 15;
		}

		return 0;
	}

}
