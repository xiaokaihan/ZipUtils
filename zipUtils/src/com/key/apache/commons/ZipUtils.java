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
 * ʹ��org.apache.commons.io.IOUtils��⣬��дѹ���ͽ�ѹ���Ĺ�����
 * 
 * @author Key.Xiao
 * @since 2016��10��19��17:47:13
 */
public class ZipUtils {

	/**
	 * ���������治Ҫ�����쳣�����쳣�׸������ߴ��� ��srcDirĿ¼�µ������ļ����ŵ�zipOutputѹ���ļ���.
	 * 
	 * ��srcDirĿ¼�µ������ļ����ŵ�zipOutputѹ���ļ��С�
	 * 
	 * @param srcDir
	 *            ��Ҫѹ�����ļ���Ŀ¼��
	 * @param zipOutput
	 *            ѹ���ļ��� ���zipOutputΪnull�����½�ZipOutput�ļ����ļ�����srcDirͬ������׺��.zip: srcDir+ ".zip"
	 * @throws IOException
	 */
	public static void createZipFileOfDirectory(File srcDir, File zipOutput) throws IOException {
		if (!srcDir.exists() || !srcDir.isDirectory()) {
			throw new IllegalArgumentException(srcDir.getAbsolutePath() + "is not a directory");
		}
		// ���zipOutputΪnull�����½�ZipOutput�ļ����ļ�����srcDirͬ������׺��.zip.
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
			// �ر���
			IOUtils.closeQuietly(zipOutputStream);
		}

	}

	/**
	 * ��Ŀ¼�µ��ļ����ŵ�ѹ���ļ���
	 * 
	 * @param dir
	 *            ��ѹ����Ŀ¼
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
	 * ��ĳѹ���ļ���ѹ��, ��ѹ��ָ��Ŀ���ļ��У� ��δָ��Ŀ���ļ��У� ���ѹ����ѹ���ļ�ͬ�����ļ����С�
	 * 
	 * @param zipFile   ����ѹ�����ļ�
	 * @param targetDir  �ɞ�null, ��ѹ���Ŀ��Ŀ¼
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
