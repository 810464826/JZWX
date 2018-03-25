package com.jz.web.common.generator;

import java.util.List;

import javax.sql.DataSource;

import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.activerecord.generator.TableMeta;

/**
 * 生产controller,service,model
 * 
 * @author lency
 *
 */
public class AdminGenerator extends Generator {
	protected ControllerGenerator controllerGenerator;

	public AdminGenerator(DataSource dataSource, String baseModelPackageName, String baseModelOutputDir,
			String modelPackageName, String modelOutputDir) {
		super(dataSource, baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
		controllerGenerator = new ControllerGenerator(modelPackageName.replace("model", "controller"),
				modelOutputDir.replace("model", "controller.admin"),modelPackageName);
	}

	public void generate() {
		long start = System.currentTimeMillis();
		List<TableMeta> tableMetas = metaBuilder.build();
		if (tableMetas.size() == 0) {
			System.out.println("TableMeta 数量为 0，不生成任何文件");
			return;
		}

		baseModelGenerator.generate(tableMetas);

		if (modelGenerator != null) {
			modelGenerator.generate(tableMetas);
		}

		if (mappingKitGenerator != null) {
			mappingKitGenerator.generate(tableMetas);
		}

		if (dataDictionaryGenerator != null && generateDataDictionary) {
			dataDictionaryGenerator.generate(tableMetas);
		}
		if (controllerGenerator != null) {
			controllerGenerator.generate(tableMetas);
		}

		long usedTime = (System.currentTimeMillis() - start) / 1000;
		System.out.println("Generate complete in " + usedTime + " seconds.");
	}
}
