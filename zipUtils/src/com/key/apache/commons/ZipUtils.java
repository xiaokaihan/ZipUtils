package com.key.apache.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

/**
 * 使用org.apache.commons.io.IOUtils类库，编写压缩和解压缩的工具类
 * 
 * @author Key.Xiao
 * @since 2016年10月19日17:47:13
 */
public class ZipUtils {

	/**
	 * 工具类里面不要捕获异常，将异常抛给调用者处理。 将srcDir目录下的所有文件都放到zipOutput压缩文件中.
	 * 
	 * 将srcDir目录下的所有文件都放到zipOutput压缩文件中。
	 * 
	 * @param srcDir
	 *            需要压缩的文件的目录名
	 * @param zipOutput
	 *            压缩文件， 如果zipOutput为null，则新建ZipOutput文件，文件名与srcDir同名，后缀加.zip: srcDir+ ".zip"
	 * @throws IOException
	 */
	public static void createZipFileOfDirectory(File srcDir, File zipOutput) throws IOException {
		if (!srcDir.exists() || !srcDir.isDirectory()) {
			throw new IllegalArgumentException(srcDir.getAbsolutePath() + "is not a directory");
		}
		// 如果zipOutput为null，则新建ZipOutput文件，文件名与srcDir同名，后缀加.zip.
		if (zipOutput == null) {
			zipOutput = new File(srcDir.getPath() + ".zip");
		}

		if (zipOutput.exists() && !zipOutput.isFile()) {
			throw new IllegalArgumentException(zipOutput.getAbsolutePath() + " exist but not a file!");
		}

		ZipOutputStream zipOutputStream = null;
		String baseName = srcDir.getAbsolutePath() + File.pathSeparator;

		try {
			zipOutputStream = new ZipOutputStream(new FileOutputStream(zipOutput));
			addDirToZip(srcDir, zipOutputStream, baseName);
		} finally {
			// 关闭流
			IOUtils.closeQuietly(zipOutputStream);
		}

	}

	/**
	 * 将目录下的文件都放到压缩文件中
	 * 
	 * @param dir
	 *            被压缩的目录
	 * @param zip
	 * @param baseName
	 * @throws IOException
	 */
	private static void addDirToZip(File dir, ZipOutputStream zip, String baseName) throws IOException {
		File[] files = dir.listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				addDirToZip(dir, zip, baseName);
			} else {
				String entryName = file.getAbsolutePath().substring(baseName.length());
				ZipEntry zipEntry = new ZipEntry(entryName);
				zip.putNextEntry(zipEntry);

				FileInputStream fileInput = new FileInputStream(file);
				try {
					IOUtils.copy(fileInput, zip);
					zip.closeEntry();
				} finally {
					IOUtils.closeQuietly(fileInput);
				}
			}
		}
	}

	/**
	 * 将某压缩文件解压缩, 解压至指定目标文件夹； 若未指定目标文件夹， 则解压至与压缩文件同名的文件夹中。
	 * 
	 * @param zipFile   被解压缩的文件
	 * @param targetDir  可為null, 解压后的目标目录
	 * @throws IOException
	 */
	public static void unzipFileToDirectory(File zipFile, File targetDir) throws IOException {
		if (!zipFile.exists() || !zipFile.isFile()) {
			throw new IllegalArgumentException(zipFile.getAbsolutePath() + "is not a file");
		}

		if (targetDir == null) {
			targetDir = new File((zipFile.getAbsoluteFile()+"").replace(".zip", ""));
			targetDir.mkdir();
		}

		if (!targetDir.exists() || !targetDir.isDirectory()) {
			throw new IllegalArgumentException(targetDir.getAbsolutePath() + "is not a Directory");
		}

		if (targetDir.listFiles().length != 0) {
			throw new IllegalArgumentException(targetDir.getAbsolutePath() + "is not empty");
		}

		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry zipEntry = zis.getNextEntry();

		while (zipEntry != null) {
			String entryFileName = zipEntry.getName();
			File newFile = new File(targetDir, entryFileName);

			if (zipEntry.isDirectory()) {
				if (!newFile.exists()) {
					newFile.mkdirs();
				} else if (!newFile.isDirectory()) {
					throw new RuntimeException(newFile.getAbsolutePath() + "already exists and is not a directory.");

				}
			} else {
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);
				try {
					IOUtils.copy(zis, fos);
				} finally {
					IOUtils.closeQuietly(fos);
				}
			}
			zipEntry = zis.getNextEntry();
		}
		zis.closeEntry();
		IOUtils.closeQuietly(zis);
	}

	/*for test**/
	/*public static void main(String[] args) {
		File dir = new File("F:/Test");
		File zipFile = new File("F:/Test.zip");
		File target = new File("F:/Test123");
		target.mkdirs();
		try {
			createZipFileOfDirectory(dir, null);
			unzipFileToDirectory(zipFile, target);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

}
