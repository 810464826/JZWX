package com.jz.web.common.generator;

import javax.sql.DataSource;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jz.web.common.config.BaseConfig;

/**
 * 运行sql逆向代码生成
 * 
 * @author lucio
 *
 */
public class _RunGenerator {

	public static void main(String[] args) {
		String basePackageName = "com.uitrs.express";
		_RunGenerator.generator(basePackageName);
	}

	public static DataSource getDataSource() {
		PropKit.use("system.properties");
		DruidPlugin dbPlugin = BaseConfig.createDruidPlugin();
		dbPlugin.start();
		return dbPlugin.getDataSource();
	}

	private static void generator(String basePackageName) {
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = basePackageName + ".model";
		// base model 所使用的包名
		String baseModelPackageName = modelPackageName + ".base";
		// base model 文件保存路径
		// String baseModelOutputDir = PathKit.getWebRootPath() +
		// "/../src/com/demo/common/model/base";
		// base model 文件保存路径
		String baseModelOutputDir = PathKit.getWebRootPath() + "/../src/" + baseModelPackageName.replaceAll("\\.", "/");
		System.err.println(baseModelOutputDir);

		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/..";

		// 创建生成器
		Generator gen = new AdminGenerator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName,
				modelOutputDir);
		// 添加不需要生成的表名
		gen.addExcludedTable("adv");
		// 设置是否在 Model 中生成 dao 对象
		gen.setGenerateDaoInModel(true);
		// 设置是否生成字典文件
		gen.setGenerateDataDictionary(false);
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为
		// "User"而非 OscUser
		gen.setRemovedTableNamePrefixes("table_");
		// 生成
		gen.generate();
	}

}
