package com.jz.web.common.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.TableMeta;

public class ControllerGenerator {
	protected String packageTemplate = "package %s;%n%n";
	protected String importTemplate = "import com.jfinal.core.Controller;%n" + "import %s.%s;%n%n";
	protected String classDefineTemplate = "/**%n" + " * %sController%n" + " * %n"
			+ " * 所有 sql 与业务逻辑写在 Model 或 Service 中，不要写在 Controller 中，养成好习惯，有利于大型项目的开发与维护%n" + " */%n"
			+ "public class %sController extends Controller {%n%n";
	protected String indexTemplate = "\tpublic void index() {%n"
			+ "\t\tsetAttr(\"%sPage\", %s.dao.paginate(getParaToInt(0, 1), 10,\"select *\", \"from %s order by id asc\"));%n"
			+ "\t\trender(\"%s/%s.html\");%n"
			+ "\t}%n%n";
	protected String addTemplate = "\tpublic void add() {%n"
			+ "\t\trender(\"%s/add.html\");%n"
			+ "\t}%n%n";
	protected String saveTemplate = "\tpublic void save() {%n"
			+"\t\tgetModel(%s.class).save();%n"
			+ "\t\tredirect(\"/%s\");%n"
			+ "\t}%n%n";
	protected String updateTemplate = "\tpublic void update() {%n"
			+"\t\tgetModel(%s.class).update();%n"
			+ "\t\tredirect(\"/%s\");%n"
			+ "\t}%n%n";
	protected String editTemplate = "\tpublic void edit() {%n"
			+"\t\tsetAttr(\"%s\", %s.dao.findById(getParaToInt()));%n"
			+ "\t\trender(\"%s/edit.html\");%n"
			+ "\t}%n%n";
	protected String detailTemplate = "\tpublic void detail() {%n"
			+"\t\tsetAttr(\"%s\", %s.dao.findById(getParaToInt()));%n"
			+ "\t\trender(\"%s/detail.html\");%n"
			+ "\t}%n%n";
	protected String deleteTemplate = "\tpublic void delete() {%n"
			+"\t\t%s.dao.deleteById(getParaToInt());%n"
			+ "\t\tredirect(\"/%s\");%n"
			+ "\t}%n%n";
	protected String controllerPackageName;

	protected String controllerOutputDir;
	protected String modelPackageName;

	public ControllerGenerator(String controllerPackageName, String controllerOutputDir, String modelPackageName) {
		if (StrKit.isBlank(controllerPackageName))
			throw new IllegalArgumentException("controllerPackageName can not be blank.");
		if (controllerPackageName.contains("/") || controllerPackageName.contains("\\"))
			throw new IllegalArgumentException("controllerPackageName error : " + controllerPackageName);
		if (StrKit.isBlank(controllerOutputDir))
			throw new IllegalArgumentException("controllerOutputDir can not be blank.");

		this.controllerPackageName = controllerPackageName;
		this.controllerOutputDir = controllerOutputDir;
		this.modelPackageName = modelPackageName;
		System.err.println(controllerPackageName);
		System.err.println(controllerOutputDir);
	}

	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate Controller ...");
		for (TableMeta tableMeta : tableMetas)
			genContent(tableMeta);
		wirtToFile(tableMetas);
	}

	protected void genContent(TableMeta tableMeta) {
		StringBuilder ret = new StringBuilder();
		genPackage(ret);
		String pathName = StrKit.firstCharToLowerCase(tableMeta.modelName);
		genImport(ret, tableMeta);
		genClassDefine(tableMeta, ret);
		genIndexMethodName(tableMeta,pathName, ret);
		genAddMethodName(tableMeta,pathName, ret);
		genSaveMethodName(tableMeta,pathName, ret);
		genEditMethodName(tableMeta,pathName, ret);
		genDetailMethodName(tableMeta,pathName, ret);
		genUpdateMethodName(tableMeta,pathName, ret);
		genDeleteMethodName(tableMeta,pathName, ret);
		ret.append(String.format("}%n"));
		tableMeta.baseModelContent = ret.toString();
	}

	private void genDeleteMethodName(TableMeta tableMeta, String pathName, StringBuilder ret) {
		String method = String.format(deleteTemplate, tableMeta.modelName, pathName);
		ret.append(method);
	}

	private void genUpdateMethodName(TableMeta tableMeta, String pathName, StringBuilder ret) {
		String method = String.format(updateTemplate, tableMeta.modelName, pathName);
		ret.append(method);
	}

	private void genDetailMethodName(TableMeta tableMeta, String pathName, StringBuilder ret) {
		String method = String.format(detailTemplate, pathName, tableMeta.modelName, pathName);
		ret.append(method);
	}

	private void genEditMethodName(TableMeta tableMeta, String pathName, StringBuilder ret) {
		String method = String.format(editTemplate, pathName, tableMeta.modelName, pathName);
		ret.append(method);
	}

	private void genSaveMethodName(TableMeta tableMeta, String pathName, StringBuilder ret) {
		String method = String.format(saveTemplate, tableMeta.modelName, pathName);
		ret.append(method);
	}

	private void genAddMethodName(TableMeta tableMeta, String pathName, StringBuilder ret) {
		String method = String.format(addTemplate, pathName);
		ret.append(method);
	}

	protected void genPackage(StringBuilder ret) {
		ret.append(String.format(packageTemplate, controllerPackageName));
	}

	protected void genImport(StringBuilder ret, TableMeta tableMeta) {
		ret.append(String.format(importTemplate, modelPackageName, tableMeta.modelName));
	}

	protected void genClassDefine(TableMeta tableMeta, StringBuilder ret) {
		ret.append(String.format(classDefineTemplate, tableMeta.modelName, tableMeta.modelName));
	}

	protected void genIndexMethodName(TableMeta tableMeta, String pathName, StringBuilder ret) {
		String method = String.format(indexTemplate, pathName, tableMeta.modelName, pathName, pathName,pathName);
		ret.append(method);
	}

	protected void wirtToFile(List<TableMeta> tableMetas) {
		try {
			for (TableMeta tableMeta : tableMetas)
				wirtToFile(tableMeta);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * base model 覆盖写入
	 */
	protected void wirtToFile(TableMeta tableMeta) throws IOException {
		File dir = new File(controllerOutputDir);
		if (!dir.exists())
			dir.mkdirs();

		String target = controllerOutputDir + File.separator + tableMeta.modelName + "Controller.java";
		FileWriter fw = new FileWriter(target);
		try {
			fw.write(tableMeta.baseModelContent);
		} finally {
			fw.close();
		}
	}
}
