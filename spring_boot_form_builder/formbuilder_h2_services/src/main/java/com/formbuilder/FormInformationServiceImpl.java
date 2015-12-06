package com.formbuilder;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.val;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.formbuilder.dto.FormInformation;
import com.formbuilder.dto.UiForm;
import com.formbuilder.service.RuleValidationOutcome;
import com.formbuilder.service.UiRuleValidatorService;

@Service
public class FormInformationServiceImpl implements FormInformationService {

	// private final FormInformationRepository repository;
	private final UiFormDao repository;
	private static Logger logger = Logger.getLogger(FormInformationServiceImpl.class);

	@Autowired
	public FormInformationServiceImpl(UiFormDao repository) {
		this.repository = repository;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.formbuilder.FormInformationService#findAllFormTemplates()
	 */
	@Override
	public List<Map> findAllFormTemplates(String appName) throws Exception {
		return repository.getFormList(appName).stream().collect(Collectors.groupingBy(UiForm::getGroupBy)).entrySet().stream().map(x -> {
			Map map1 = new LinkedHashMap();
			map1.put("group", x.getKey());
			map1.put("tableList", x.getValue());
			return map1;
		}).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.formbuilder.FormInformationService#save(com.formbuilder.dao.
	 * FormInformation)
	 */
	@Override
	public JSONObject save(JSONObject input, String appName, int formId, int dataId) {
		JSONObject json = new JSONObject();
		try {
			List<RuleValidationOutcome> outcomes = repository.saveFormData(appName, formId, dataId, input);
			json.put("success", UiRuleValidatorService.success(outcomes));
			json.put("outcomeList", outcomes);
		} catch (ParseException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.formbuilder.FormInformationService#getData(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public JSONObject getData(String appName, String formName, String dataid) throws JsonParseException, JsonMappingException,
			IOException {
		try {
			val form = repository.getFormInfo(appName, Integer.valueOf(formName));
			val formLinks = repository.getFormLinkInfo(appName, Integer.valueOf(formName));

			val json = repository.getFormData(appName, Integer.valueOf(dataid), form, formLinks);
			val json1 = new LinkedHashMap();
			json1.put("fields", json);
			json1.put("type", "fieldset");
			json1.put("label", form.getDisplayName());
			val json2 = new LinkedHashMap();
			json2.put(form.getFormTableName(), json1);

			JSONObject json3 = new JSONObject(json2);

			return json3;
		} catch (NumberFormatException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.formbuilder.FormInformationService#findAllDataByNames(java.lang.String
	 * )
	 */
	@Override
	public LinkedHashMap findAllDataByNames(String appName, String formName) {
		val formInfo = repository.getFormInfo(appName, Integer.valueOf(formName));

		List<Map> list = repository.getFormDataList(appName, Integer.valueOf(formName));

		val map = new LinkedHashMap();
		map.put("formName", formInfo.getDisplayName());
		map.put("formList", list);

		return map;
	}

	@Override
	public void deleteRecord(String appName, int rowId, int formId) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		logger.debug("test delete");
		logger.debug("Table id " + formId);
		logger.debug("row id " + rowId);
		try {
			repository.deleteRow(appName, rowId, formId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean hideDesigner() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getApplicationDisplayName(String appName) {
		return repository.getApplicationDisplayName(appName);
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FormInformation findTemplateByName(String appName, String name) {
		// TODO Auto-generated method stub
		return null;
	}
}