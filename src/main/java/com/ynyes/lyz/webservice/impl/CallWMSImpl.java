/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
// START SNIPPET: service
package com.ynyes.lyz.webservice.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jws.WebService;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.geronimo.mail.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ynyes.lyz.entity.TdDeliveryInfo;
import com.ynyes.lyz.entity.TdDeliveryInfoDetail;
import com.ynyes.lyz.entity.TdGoods;
import com.ynyes.lyz.service.TdDeliveryInfoDetailService;
import com.ynyes.lyz.service.TdDeliveryInfoService;
import com.ynyes.lyz.service.TdGoodsService;
import com.ynyes.lyz.service.TdInterfaceErrorLogService;
import com.ynyes.lyz.service.TdOrderService;
import com.ynyes.lyz.service.TdRequisitionGoodsService;
import com.ynyes.lyz.service.TdRequisitionService;
import com.ynyes.lyz.webservice.ICallWMS;

@WebService
public class CallWMSImpl implements ICallWMS {
	
	@Autowired
	private TdDeliveryInfoService tdDeliveryInfoService;
	
	@Autowired
	private TdDeliveryInfoDetailService tdDeliveryInfoDetailService;
	
	@Autowired
	private TdGoodsService tdGoodsService;

	public String GetWMSInfo(String STRTABLE, String STRTYPE, String XML)
	{
		System.out.println("getWMSInfo called：");

		if (null == STRTABLE || STRTABLE.isEmpty() || STRTABLE.equals("?"))
		{
			return "<RESULTS><STATUS><CODE>1</CODE><MESSAGE>STRTABLE参数错误</MESSAGE></STATUS></RESULTS>";
		}

		if (null == XML || XML.isEmpty() || XML.equals("?")) 
		{
			return "<RESULTS><STATUS><CODE>1</CODE><MESSAGE>XML参数错误</MESSAGE></STATUS></RESULTS>";
		}
		
		
		String XMLStr = XML.trim();
		
		XMLStr = XML.replace("\n", "");
		
		byte[] decoded = Base64.decode(XMLStr);

		String decodedXML = null;

		try
		{
			decodedXML = new String(decoded, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			System.out.println("UnsupportedEncodingException for decodedXML");
			e.printStackTrace();
		}
		
//		decodedXML = decodedXML.
//		StringBuffer sb = new StringBuffer();  
//	    sb.append(decodedXML);  
//	    String xmString = "";  
//	    String xmlUTF8="";  
//	    try {  
//	    xmString = new String(sb.toString().getBytes("UTF-8"));  
//	    xmlUTF8 = URLEncoder.encode(xmString, "UTF-8");  
//	    System.out.println("utf-8 编码：" + xmlUTF8) ;  
//	    } catch (UnsupportedEncodingException e) {  
//	    // TODO Auto-generated catch block  
//	    e.printStackTrace();  
//	    }  

		if (null == decodedXML || decodedXML.isEmpty()) 
		{
			return "<RESULTS><STATUS><CODE>1</CODE><MESSAGE>解密后XML数据为空</MESSAGE></STATUS></RESULTS>";
		}

		System.out.println(decodedXML);

		// 解析XML
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try
		{
			builder = factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) 
		{
			e.printStackTrace();
			return "<RESULTS><STATUS><CODE>1</CODE><MESSAGE>解密后xml参数错误</MESSAGE></STATUS></RESULTS>";
		}

		Document document = null;

		InputSource is = new InputSource(new StringReader(decodedXML));

		try
		{
			document = builder.parse(is);
		}
		catch (SAXException | IOException e)
		{
			e.printStackTrace();
			return "<RESULTS><STATUS><CODE>1</CODE><MESSAGE>解密后xml格式不对</MESSAGE></STATUS></RESULTS>";
		}
		NodeList nodeList = document.getElementsByTagName("TABLE");
		if (STRTABLE.equalsIgnoreCase("tbw_send_task_m"))
		{
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				String c_task_no = null;//任务编号
				String c_begin_dt = null;//开始时间
				String c_end_dt = null;//结束时间
				String c_wh_no = null;//仓库编号
				String c_op_status = null;//操作状态(初始、作业中、完成、结案)
				String c_op_user = null;//作业人员
				String c_modified_userno = null;//修改人员
				String c_owner_no = null;//委托业主
				String c_reserved1 = null;//主单号
				
				Node node = nodeList.item(i);
				NodeList childNodeList = node.getChildNodes();

				for (int idx = 0; idx < childNodeList.getLength(); idx++)
				{
 					Node childNode = childNodeList.item(idx);
					
					if (childNode.getNodeType() == Node.ELEMENT_NODE) 
					{
						// 比较字段名
						if (childNode.getNodeName().equalsIgnoreCase("c_task_no"))
						{
							// 有值
							if (null != childNode.getChildNodes().item(0))
							{
								c_task_no = childNode.getChildNodes().item(0).getNodeValue();
							}
							// 空值
							else
							{
								c_task_no = null;
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_begin_dt"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								c_begin_dt = childNode.getChildNodes().item(0).getNodeValue();
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_end_dt"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								c_end_dt = childNode.getChildNodes().item(0).getNodeValue();
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_wh_no"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								c_wh_no = childNode.getChildNodes().item(0).getNodeValue();
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_op_status"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								c_op_status = childNode.getChildNodes().item(0).getNodeValue();
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_op_user"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								c_op_user = childNode.getChildNodes().item(0).getNodeValue();
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_modified_userno"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								c_modified_userno = childNode.getChildNodes().item(0).getNodeValue();
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_owner_no"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								c_owner_no = childNode.getChildNodes().item(0).getNodeValue();
							}
						}
					}
				}
				
				//保存 修改
				TdDeliveryInfo tdDeliveryInfo = tdDeliveryInfoService.findByTaskNo(c_task_no);
				if (tdDeliveryInfo == null)
				{
					tdDeliveryInfo = new TdDeliveryInfo();
				}
				tdDeliveryInfo.setTaskNo(c_task_no);
				tdDeliveryInfo.setWhNo(c_wh_no);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
				if (c_begin_dt != null)
				{
					try 
					{
						Date startdate = sdf.parse(c_begin_dt);
						tdDeliveryInfo.setBeginDt(startdate);
					}
					catch (ParseException e) 
					{
						e.printStackTrace();
					}
				}
				if (c_end_dt != null)
				{
					try 
					{
						Date enddate = sdf.parse(c_end_dt);
						tdDeliveryInfo.setEndDt(enddate);
					}
					catch (ParseException e) 
					{
						e.printStackTrace();
					}
				}
				tdDeliveryInfo.setOpStatus(c_op_status);
				tdDeliveryInfo.setOpUser(c_op_user);
				tdDeliveryInfo.setModifiedUserno(c_modified_userno);
				tdDeliveryInfo.setOwnerNo(c_owner_no);
				tdDeliveryInfoService.save(tdDeliveryInfo);
				
			}
			return "<RESULTS><STATUS><CODE>0</CODE><MESSAGE></MESSAGE></STATUS></RESULTS>";
		}
		else if (STRTABLE.equalsIgnoreCase("tbw_send_task_d"))
		{
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				String c_task_no = null;//任务编号
				String c_begin_dt = null;//开始时间
				String c_end_dt = null;//结束时间
				String c_wh_no = null;//仓库编号
				String c_op_status = null;//操作状态(初始、作业中、完成、结案)
				String c_op_user = null;//作业人员
				String c_modified_userno = null;//修改人员
				String c_owner_no = null;//委托业主
				String c_gcode = null;//商品编号
				Double c_d_ack_qty = null; //实回数量
				Double c_d_request_qty = null;//请求数量
				
				
				Node node = nodeList.item(i);
				NodeList childNodeList = node.getChildNodes();

				for (int idx = 0; idx < childNodeList.getLength(); idx++)
				{
 					Node childNode = childNodeList.item(idx);
					
					if (childNode.getNodeType() == Node.ELEMENT_NODE) 
					{
						// 比较字段名
						if (childNode.getNodeName().equalsIgnoreCase("c_task_no"))
						{
							// 有值
							if (null != childNode.getChildNodes().item(0))
							{
								c_task_no = childNode.getChildNodes().item(0).getNodeValue();
							}
							// 空值
							else
							{
								c_task_no = null;
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_begin_dt"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								c_begin_dt = childNode.getChildNodes().item(0).getNodeValue();
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_end_dt"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								c_end_dt = childNode.getChildNodes().item(0).getNodeValue();
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_wh_no"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								c_wh_no = childNode.getChildNodes().item(0).getNodeValue();
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_op_status"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								c_op_status = childNode.getChildNodes().item(0).getNodeValue();
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_op_user"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								c_op_user = childNode.getChildNodes().item(0).getNodeValue();
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_modified_userno"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								c_modified_userno = childNode.getChildNodes().item(0).getNodeValue();
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_owner_no"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								c_owner_no = childNode.getChildNodes().item(0).getNodeValue();
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_gcode"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								c_gcode = childNode.getChildNodes().item(0).getNodeValue();
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_d_ack_qty"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								c_d_ack_qty = Double.parseDouble(childNode.getChildNodes().item(0).getNodeValue());
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_d_request_qty"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								String string =  childNode.getChildNodes().item(0).getNodeValue();
								c_d_request_qty = Double.parseDouble(string);
							}
						}
						
					}
				}
				
				//保存 修改
				TdDeliveryInfoDetail infoDetail = new TdDeliveryInfoDetail();
				infoDetail.setTaskNo(c_task_no);
				infoDetail.setWhNo(c_wh_no);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if (c_begin_dt != null)
				{
					try 
					{
						Date startdate = sdf.parse(c_begin_dt);
						infoDetail.setBeginDt(startdate);
					}
					catch (ParseException e) 
					{
						e.printStackTrace();
					}
				}
				if (c_end_dt != null)
				{
					try 
					{
						Date enddate = sdf.parse(c_end_dt);
						infoDetail.setEndDt(enddate);
					}
					catch (ParseException e) 
					{
						e.printStackTrace();
					}
				}
				infoDetail.setOpStatus(c_op_status);
				infoDetail.setOpUser(c_op_user);
				infoDetail.setModifiedUserno(c_modified_userno);
				infoDetail.setOwnerNo(c_owner_no);
				infoDetail.setgCode(c_gcode);
				infoDetail.setRequstNumber(c_d_request_qty);
				infoDetail.setBackNumber(c_d_ack_qty);
				tdDeliveryInfoDetailService.save(infoDetail);
				
				Long backquantity = Math.round(infoDetail.getBackNumber() == null ? 0 : infoDetail.getBackNumber());
				TdGoods tdGoods = tdGoodsService.findByCode(infoDetail.getgCode());
				if (tdGoods != null && tdGoods.getLeftNumber() != null)
				{
					tdGoods.setLeftNumber(tdGoods.getLeftNumber() - backquantity);
					tdGoodsService.save(tdGoods, "goods");
				}
			}
			return "<RESULTS><STATUS><CODE>0</CODE><MESSAGE></MESSAGE></STATUS></RESULTS>";
		}
		return "<RESULTS><STATUS><CODE>1</CODE><MESSAGE>不支持该表数据传输："+ STRTABLE +"</MESSAGE></STATUS></RESULTS>";
	}
	
//	private TdRequisitionGoods saveRequisitionGoodsFromOrderGoods(TdOrderGoods orderGoods)
//	{
//		TdRequisitionGoods requisitionGoods = new TdRequisitionGoods();
//		requisitionGoods.setGoodsCode(orderGoods.getSku());
//		requisitionGoods.setGoodsTitle(orderGoods.getGoodsTitle());
//		requisitionGoods.setPrice(orderGoods.getPrice());
//		requisitionGoods.setQuantity(orderGoods.getQuantity());
//		requisitionGoods.setSubOrderNumber(tdOrder.getOrderNumber());
//		requisitionGoods.setOrderNumber(mainOrderNumber);
//		tdRequisitionGoodsService.save(requisitionGoods);
//	}
	
}
// END SNIPPET: service
