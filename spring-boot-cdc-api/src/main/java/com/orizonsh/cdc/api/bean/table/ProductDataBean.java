package com.orizonsh.cdc.api.bean.table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductDataBean extends TableDataBeanBase {

	/** ID */
	@JsonProperty(value = "id")
	private Long id;

	/** 商品名 */
	@JsonProperty(value = "product_name")
	private String productName;
}
