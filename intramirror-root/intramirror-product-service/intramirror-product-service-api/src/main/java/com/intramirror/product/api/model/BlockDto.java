package com.intramirror.product.api.model;

import java.util.List;

public class BlockDto extends Block{
	
		private List<BlockContentTemplateRel> blockContentTemplateRelList;
		
		public List<BlockContentTemplateRel> getBlockContentTemplateRelList() {
			return blockContentTemplateRelList;
		}

		public void setBlockContentTemplateRelList(List<BlockContentTemplateRel> blockContentTemplateRelList) {
			this.blockContentTemplateRelList = blockContentTemplateRelList;
		}

}
