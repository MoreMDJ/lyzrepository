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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.wsdl.TParam;
import org.apache.geronimo.mail.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ynyes.lyz.entity.TdDeliveryInfo;
import com.ynyes.lyz.entity.TdDeliveryInfoDetail;
import com.ynyes.lyz.entity.TdInterfaceErrorLog;
import com.ynyes.lyz.entity.TdOrder;
import com.ynyes.lyz.entity.TdOrderGoods;
import com.ynyes.lyz.entity.TdRequisition;
import com.ynyes.lyz.entity.TdRequisitionGoods;
import com.ynyes.lyz.service.TdDeliveryInfoDetailService;
import com.ynyes.lyz.service.TdDeliveryInfoService;
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
	private TdOrderService tdOrderService;
	
	@Autowired
	private TdRequisitionService tdRequisitionService;
	
	@Autowired
	private TdRequisitionGoodsService tdRequisitionGoodsService;
	
	@Autowired
	private TdInterfaceErrorLogService tdInterfaceErrorLogService;
	
	@Autowired
	private TdDeliveryInfoDetailService tdDeliveryInfoDetailService;

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
				Integer c_d_ack_qty = null; //实回数量
				Integer c_d_request_qty = null;//请求数量
				
				
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
								c_d_ack_qty = Integer.parseInt(childNode.getChildNodes().item(0).getNodeValue());
							}
						}
						else if (childNode.getNodeName().equalsIgnoreCase("c_d_request_qty"))
						{
							if (null != childNode.getChildNodes().item(0))
							{
								c_d_request_qty = Integer.parseInt(childNode.getChildNodes().item(0).getNodeValue());
							}
						}
						
					}
				}
				
				//保存 修改
				TdDeliveryInfoDetail infoDetail = new TdDeliveryInfoDetail();
				infoDetail.setTaskNo(c_task_no);
				infoDetail.setWhNo(c_wh_no);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
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
			}
			return "<RESULTS><STATUS><CODE>0</CODE><MESSAGE></MESSAGE></STATUS></RESULTS>";
		}
		return "<RESULTS><STATUS><CODE>1</CODE><MESSAGE>不支持该表数据传输："+ STRTABLE +"</MESSAGE></STATUS></RESULTS>";
	}
	
	// TODO Client
	public void sendMsgToWMS(List<TdOrder> orderList,String mainOrderNumber)
	{
		if (orderList.size() <= 0)
		{
			return ;
		}
		if (mainOrderNumber == null || mainOrderNumber.equalsIgnoreCase(""))
		{
			return ;
		}
		TdRequisition requisition = SaveRequisiton(orderList, mainOrderNumber);
		
//		String JAVA_PATH = System.getenv("JAVA_HOME");
//		System.err.println("JAVA_PATH:"+JAVA_PATH);
//		String PATH = System.getenv("Path");
//		System.err.println("PATH:" + PATH);
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();  
		org.apache.cxf.endpoint.Client client = dcf.createClient("http://182.92.160.220:8199/WmsInterServer.asmx?wsdl");
		//url为调用webService的wsdl地址
		QName name = new QName("http://tempuri.org/","GetErpInfo");
		//paramvalue为参数值 
		Object[] objects = null;
		if (requisition != null)
		{
			for (TdRequisitionGoods requisitionGoods : requisition.getRequisiteGoodsList())
			{
				String xmlGoodsEncode = XMLMakeAndEncode(requisitionGoods, 2);
				try
				{
					objects = client.invoke(name,"td_requisition_goods","1",xmlGoodsEncode);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					writeErrorLog(mainOrderNumber, requisitionGoods.getSubOrderNumber(), e.getMessage());
//					return "发送异常";	
				}
				String result = null;
				if (objects != null)
				{
					for (Object object : objects) 
					{
						result += object;
					}
				}
				Map<String, String> resultMap = chectResult(result);
				if (resultMap.get("status").equalsIgnoreCase("Y"))
				{
				}
				else
				{
					writeErrorLog(mainOrderNumber, requisitionGoods.getSubOrderNumber(), resultMap.get("msg"));
				}
			}
			String xmlEncode = XMLMakeAndEncode(requisition, 1);
			try
			{
	        	objects = client.invoke(name,"td_requisition","1",xmlEncode);
	        }
			catch (Exception e)
			{
	        	e.printStackTrace();
	        	writeErrorLog(mainOrderNumber, "无", e.getMessage());
//	        	return "发送异常";
	        }
			String result = null;
			if (objects != null)
			{
				for (Object object : objects) 
				{
					result += object;
				}
			}
			Map<String, String> resultMap = chectResult(result);
			if (resultMap.get("status").equalsIgnoreCase("Y"))
			{
			}
			else
			{
				writeErrorLog(mainOrderNumber, "无", resultMap.get("msg"));
			}
		}
	}
	
	
	/**
	 * 保存要货单
	 * @param orderList
	 * @param mainOrderNumber
	 * @return
	 */
	private TdRequisition SaveRequisiton(List<TdOrder> orderList,String mainOrderNumber)
	{
		if (orderList.size() <= 0)
		{
			return null;
		}
		TdOrder order = orderList.get(0);
		
		TdRequisition requisition = tdRequisitionService.findByOrderNumber(mainOrderNumber);
		if (requisition == null)
		{
			requisition = new TdRequisition();
			requisition.setDiySiteId(order.getDiySiteId());
			requisition.setDiySiteTitle(order.getDiySiteName());
			requisition.setCustomerName(order.getUsername());
			requisition.setCustomerId(order.getUserId());
			requisition.setOrderNumber(mainOrderNumber);
			requisition.setReceiveName(order.getShippingName());
			requisition.setReceiveAddress(order.getShippingAddress());
			requisition.setReceivePhone(order.getShippingPhone());
			requisition.setTotalPrice(order.getTotalPrice());
			requisition.setTypeId(1L);
			String dayTime = order.getDeliveryDate();
			dayTime = dayTime + " " + order.getDeliveryDetailId() + ":30";
			requisition.setDeliveryTime(dayTime);
			
			List<TdRequisitionGoods> requisitionGoodsList = new ArrayList<>();
			for (TdOrder tdOrder : orderList) 
			{
				for (TdOrderGoods orderGoods : tdOrder.getOrderGoodsList())
				{
					TdRequisitionGoods requisitionGoods = new TdRequisitionGoods();
					requisitionGoods.setGoodsCode(orderGoods.getSku());
					requisitionGoods.setGoodsTitle(orderGoods.getGoodsTitle());
					requisitionGoods.setPrice(orderGoods.getPrice());
					requisitionGoods.setQuantity(orderGoods.getQuantity());
					requisitionGoods.setSubOrderNumber(tdOrder.getOrderNumber());
					requisitionGoods.setOrderNumber(mainOrderNumber);
					tdRequisitionGoodsService.save(requisitionGoods);
					requisitionGoodsList.add(requisitionGoods);
				}
				for (TdOrderGoods orderGoods : tdOrder.getGiftGoodsList())
				{
					TdRequisitionGoods requisitionGoods = new TdRequisitionGoods();
					requisitionGoods.setGoodsCode(orderGoods.getSku());
					requisitionGoods.setGoodsTitle(orderGoods.getGoodsTitle());
					requisitionGoods.setPrice(orderGoods.getPrice());
					requisitionGoods.setQuantity(orderGoods.getQuantity());
					requisitionGoods.setSubOrderNumber(tdOrder.getOrderNumber());
					requisitionGoods.setOrderNumber(mainOrderNumber);
					tdRequisitionGoodsService.save(requisitionGoods);
					requisitionGoodsList.add(requisitionGoods);
				}
				for (TdOrderGoods orderGoods : tdOrder.getPresentedList())
				{
					TdRequisitionGoods requisitionGoods = new TdRequisitionGoods();
					requisitionGoods.setGoodsCode(orderGoods.getSku());
					requisitionGoods.setGoodsTitle(orderGoods.getGoodsTitle());
					requisitionGoods.setPrice(orderGoods.getPrice());
					requisitionGoods.setQuantity(orderGoods.getQuantity());
					requisitionGoods.setSubOrderNumber(tdOrder.getOrderNumber());
					requisitionGoods.setOrderNumber(mainOrderNumber);
					tdRequisitionGoodsService.save(requisitionGoods);
					requisitionGoodsList.add(requisitionGoods);
				}
			}
			requisition.setRequisiteGoodsList(requisitionGoodsList);
			requisition = tdRequisitionService.save(requisition);
		}
		return requisition;
	}
	
	/**
	 * 根据传进来的类型返回相应的XML
	 * @param object
	 * @param type  1：tdRequisition  2：tdRequisitionGoods
	 * @return
	 */
	private String XMLMakeAndEncode(Object object,Integer type)
	{
		if (type == 1)
		{
			TdRequisition requisition = (TdRequisition)object;
			String xmlStr = "<DLAPP>"
					+"<TABLE>"
					+"<ID>"+ requisition.getId() +"</ID>"
					+"<diySiteTitle>"+ requisition.getDiySiteTitle() +"</diySiteTitle>"
					+"<diySiteId>"+ requisition.getDiySiteId() +"</diySiteId>"
					+"<customerName>"+ requisition.getCustomerName() +"</customerName>"
					+"<customerId>"+ requisition.getCustomerId() +"</customerId>"
					+"<orderNumber>"+ requisition.getOrderNumber() +"</orderNumber>"
					+"<totalPrice>"+ requisition.getTotalPrice() +"</totalPrice>"
					+"<deliveryTime>"+ requisition.getDeliveryTime() +"</deliveryTime>"
					+"<receiveName>"+ requisition.getReceiveName() +"</receiveName>"
					+"<receiveAddress>"+ requisition.getReceiveAddress() +"</receiveAddress>"
					+"<receivePhone>"+ requisition.getReceivePhone() +"</receivePhone>"
					+"<orderTime>"+ requisition.getOrderTime() +"</orderTime>"
					+"<typeId>"+ requisition +"</typeId>"
					+"</TABLE>"
					+"</DLAPP>";
			
			String encodeXML = null;
			byte[] bs = xmlStr.getBytes();
			byte[] encodeByte = Base64.encode(bs);
			try 
			{
				encodeXML = new String(encodeByte, "UTF-8");
			}
			catch (UnsupportedEncodingException e1)
			{
				System.err.println("MDJ_WMS:XML 编码出错!");
				return "FAILED";
			}
		}
		if (type == 2)
		{
			TdRequisitionGoods requisitionGoods = (TdRequisitionGoods)object;
			String xmlStr = "<DLAPP>"
							+"<TABLE>"
							+"<ID>"+ requisitionGoods.getId() +"</ID>"
							+"<goodsCode>"+ requisitionGoods.getGoodsCode() +"</goodsCode>"
							+"<goodsTitle>"+ requisitionGoods.getGoodsTitle() +"</goodsTitle>"
							+"<price>"+ requisitionGoods.getPrice() +"</price>"
							+"<quantity>"+ requisitionGoods.getQuantity() +"</quantity>"
							+"<orderNumber>"+ requisitionGoods.getOrderNumber() +"</orderNumber>"
							+"<subOrderNumber>"+ requisitionGoods.getSubOrderNumber() +"</subOrderNumber>"
							+"</TABLE>"
							+"</DLAPP>";
			
			String encodeXML = null;
			byte[] bs = xmlStr.getBytes();
			byte[] encodeByte = Base64.encode(bs);
			try 
			{
				encodeXML = new String(encodeByte, "UTF-8");
			}
			catch (UnsupportedEncodingException e1)
			{
				System.err.println("MDJ_WMS:XML 编码出错!");
				return "FAILED";
			}
		}
		return "";
	}
	
	/**
	 * 判断接口返回状态
	 * @param resultStr
	 * @return
	 */
	private Map<String, String> chectResult(String resultStr)
	{
		Map<String , String> map = new HashMap<String,String>();
		map.put("status", "n");
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
			map.put("msg", "返回参数错误 -1");
			return map;
		}

		Document document = null;

		InputSource is = new InputSource(new StringReader(resultStr));

		try
		{
			document = builder.parse(is);
		} 
		catch (SAXException | IOException e)
		{
			e.printStackTrace();
			map.put("msg", "返回参数错误 -2");
		}
//		return "<RESULTS><STATUS><CODE>1</CODE><MESSAGE>XML参数错误</MESSAGE></STATUS></RESULTS>";
		NodeList nodeList = document.getElementsByTagName("STATUS");
		Node node = nodeList.item(0);
		NodeList childNodeList = node.getChildNodes();
		Node nodeCode = childNodeList.item(0);
		Integer code = Integer.parseInt(nodeCode.getNodeValue());
		if (code == 1)
		{
			String messageStr = childNodeList.item(1).getNodeValue();
			map.put("msg", messageStr);
			return map;
		}
		map.put("status", "y");
		return map;
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

	
	private void writeErrorLog(String orderNumber,String subOrderNumber,String errorMsg)
	{
		TdInterfaceErrorLog errorLog = new TdInterfaceErrorLog();
		errorLog.setErrorMsg(errorMsg);
		errorLog.setOrderNumber(orderNumber);
		errorLog.setSubOrderNumber(subOrderNumber);
		tdInterfaceErrorLogService.save(errorLog);
	}
}
// END SNIPPET: service
