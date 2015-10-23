/**
 * 
 */
package com.yoju360.mgmt.core.image;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import magick.MagickException;
import magick.MagickImage;
import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yoju360.mgmt.core.storage.Storage;
import com.yoju360.mgmt.core.storage.StorageFile;

/**
 * @author evan.wu
 *
 */
public class ImageUtils {
	private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);
	private static final String TEMP_DIR = "./working_temp_image/";
	public static boolean useImageMagick = true;
	
	private static Storage storage = null;
	
	public static class IllegalSizeException extends RuntimeException {
		private static final long serialVersionUID = -3988299603768880366L;
		public IllegalSizeException(String msg) {
			super(msg);
		}
	}
	static {
        if (System.getProperty("jmagick.systemclassloader") == null) {
            System.setProperty("jmagick.systemclassloader", "no");
        }
        try {
        	new magick.ImageInfo();
        } catch (Throwable e) {
        	useImageMagick = false;
        	logger.warn("Make sure JMagick libraries are available in java.library.path. Current value: ");  
        	logger.warn("java.library.path=" + System.getProperty("java.library.path"));
    		if (GraphicsEnvironment.isHeadless() && GraphicsEnvironment.getLocalGraphicsEnvironment()==null) {
    			e.printStackTrace();
    			throw new Error("Both ImageMagick(JMagick) and Screen support is missing");
    		}
        }
        logger.info("java.library.path: " + System.getProperty("java.library.path"));
        System.out.println("java.library.path: " + System.getProperty("java.library.path"));
        File tempDir = new File(TEMP_DIR);
        if (!tempDir.exists())
        	tempDir.mkdirs();
        System.out.println("working image temp dir: " + tempDir.getAbsolutePath());
        logger.info("working image temp dir: " + tempDir.getAbsolutePath());
        if (!useImageMagick)
        	logger.warn("Not using ImageMagick(jmagick)!");
    }  
	
	public static void setStorage(Storage storage) {
		ImageUtils.storage = storage;
	}
	
	private static Storage getStorage() {
		return storage;
	}
	
	public static class ImageInfo {
		public String name;
		public int width;
		public int height;
		public String url;
	}
	
	/**
	 * 复制两个stream并在完成后关闭它们。
	 * @param in
	 * @param out
	 */
	private static void copyStream(InputStream in, OutputStream out) {
		try {
			int i = -1;
			while ((i=in.read())!=-1) {
				out.write(i);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			logger.error("Failed to copy stream", e);
		}
	}
	
	public static MagickImage getMagickImage(InputStream in) throws FileNotFoundException, MagickException {
		File tmp = new File(TEMP_DIR + System.nanoTime());
    	copyStream(in, new FileOutputStream(tmp));
    	MagickImage original = new MagickImage(new magick.ImageInfo(tmp.getAbsolutePath()));
    	tmp.delete();
    	return original;
	}
	
	/**
	 * @param originalImagePath 原图的相对路径+文件名
	 * @param width 目标宽
	 * @param height 目标高（不限制高则设置-1）
	 * @param keepRatio 是否保持比例
	 * @param cutCenter 截取中央区域
	 * @return image_url, image_width, image_height
	 */
	public static Map<String, Object> createScaledImage(String originalImagePath, int width, int height, boolean keepRatio, boolean cutCenter) {
		if (!useImageMagick)
			return createScaledImage1(originalImagePath, width, height, keepRatio, cutCenter);
		MagickImage original = null;
		try {
			int idx = originalImagePath.lastIndexOf(".");
			String fileExt = originalImagePath.substring(idx);
			String name = originalImagePath.substring(originalImagePath.lastIndexOf("/")+1, idx);
			StorageFile oFile = getStorage().getFile(originalImagePath);
			StorageFile newSf = null;
			original = getMagickImage(oFile.getInputStream());
			int origH = original.getDimension().height;
			int origW = original.getDimension().width;
			
			int imageWidth = 0, imageHeight = 0;

			if (keepRatio) {
				if (height<=0) {
					if (origW<=width) { // 比原图还大, copy
						InputStream oIn = oFile.getInputStream();
						newSf = getStorage().createFileWithName(oFile.getPath(), name + "_" + width + "w" + fileExt);
						copyStream(oIn, newSf.getOutputStream());
						imageWidth = origW;
						imageHeight = origH;
					} else {
						imageWidth = width;
						imageHeight = Math.round((float)width/origW * origH);
						
						MagickImage newImg = original.scaleImage(imageWidth, imageHeight);
						String tempFile = TEMP_DIR + name + "_" + width + "w" + fileExt;
						newImg.setFileName(tempFile);
						newImg.writeImage(new magick.ImageInfo());
						
						newSf = getStorage().createFileWithName(oFile.getPath(), name + "_" + width + "w" + fileExt);
						OutputStream out = newSf.getOutputStream();
						copyStream(new FileInputStream(tempFile), out);
						new File(tempFile).delete();
					}
				} else if (width<=0) {
					if (origH<=height) { // 比原图还大, copy
						InputStream oIn = oFile.getInputStream();
						newSf = getStorage().createFileWithName(oFile.getPath(), name + "_" + height + "h" + fileExt);
						copyStream(oIn, newSf.getOutputStream());
						imageWidth = origW;
						imageHeight = origH;
					} else {
						imageWidth = Math.round((float)height/origH * origW);
						imageHeight = height;
						
						MagickImage newImg = original.scaleImage(imageWidth, imageHeight);
						String tempFile = TEMP_DIR + name + "_" + height + "h" + fileExt;
						newImg.setFileName(tempFile);
						newImg.writeImage(new magick.ImageInfo());
						
						newSf = getStorage().createFileWithName(oFile.getPath(), name + "_" + height + "h" + fileExt);
						OutputStream out = newSf.getOutputStream();
						copyStream(new FileInputStream(tempFile), out);
						new File(tempFile).delete();
					}
				} else { // not keeping ratio
					imageWidth = width;
					imageHeight = height;
					
					MagickImage newImg = original.scaleImage(imageWidth, imageHeight);
					String tempFile = TEMP_DIR + name + "_" + width + "x" + height + fileExt;
					newImg.setFileName(tempFile);
					newImg.writeImage(new magick.ImageInfo());
					
					newSf = getStorage().createFileWithName(oFile.getPath(), name + "_" + width + "x" + height + fileExt);
					OutputStream out = newSf.getOutputStream();
					copyStream(new FileInputStream(tempFile), out);
					new File(tempFile).delete();
				}
			} else if (cutCenter) { //截取中间区域
				if (origH<height)
					throw new IllegalSizeException("图片高度必须大于"+height);
				if (origW<width)
					throw new IllegalSizeException("图片宽度必须大于"+width);
				
				int x = 0, y =0;
				int sourceW = origW, sourceH = origH;
				float ratio = (float)width/height;
				float oldRatio = (float)origW/origH;
				if (ratio>=oldRatio) {
					sourceH = Math.round((float)origW/width*height);
					y = Math.round(origH/2 - (height/2) * ((float)origW/width)); // 取景框放大到宽与原图一样
				} else if (ratio<oldRatio){
					sourceW = Math.round((float)origH/height*width);
					x = Math.round(origW/2 - (width/2) * ((float)origH/height)); // 取景框放大到高与原图一样
				}
				imageWidth = width;
				imageHeight = height;
				MagickImage newImg = original.cropImage(new Rectangle(x,y,sourceW,sourceH)).scaleImage(width, height);
				String tempFile = TEMP_DIR + name + "_" + width + "x" + height + fileExt;
				newImg.setFileName(tempFile);
				newImg.writeImage(new magick.ImageInfo());
				
				newSf = getStorage().createFileWithName(oFile.getPath(), name + "_" + width + "x" + height + fileExt);
				OutputStream out = newSf.getOutputStream();
				copyStream(new FileInputStream(tempFile), out);
				new File(tempFile).delete();
			} else {
				imageWidth = width;
				imageHeight = (height <= 0 ? origH : height);
				
				MagickImage newImg = original.scaleImage(imageWidth, imageHeight);
				String tempFile = TEMP_DIR + name + "_" + imageWidth + "x" + imageHeight + fileExt;
				newImg.setFileName(tempFile);
				newImg.writeImage(new magick.ImageInfo());
				
				newSf = getStorage().createFileWithName(oFile.getPath(), name + "_" + imageWidth + "x" + imageHeight + fileExt);
				OutputStream out = newSf.getOutputStream();
				copyStream(new FileInputStream(tempFile), out);
				new File(tempFile).delete();
			}
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("image_url", newSf.getFullName());
			params.put("image_width", imageWidth);
			params.put("image_height", imageHeight);
			return params;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (MagickException e) {
			throw new RuntimeException(e);
		} finally {
			if (original!=null)
				original.destroyImages();
		}
	}
	
	/**
	 * @param originalImagePath 原图的相对路径+文件名
	 * @param width 目标宽
	 * @param height 目标高（不限制高则设置-1）
	 * @param keepRatio 是否保持比例
	 * @param cutCenter 截取中央区域
	 * @return image_url, image_width, image_height
	 */
	private static Map<String, Object> createScaledImage1(String originalImagePath, int width, int height, boolean keepRatio, boolean cutCenter) {
		try {
			int idx = originalImagePath.lastIndexOf(".");
			String fileExt = originalImagePath.substring(idx);
			String name = originalImagePath.substring(originalImagePath.lastIndexOf("/")+1, idx);
			StorageFile oFile = getStorage().getFile(originalImagePath);
			StorageFile newSf = null;
			InputStream oIn = oFile.getInputStream();
			//BufferedImage original = ImageIO.read(oIn); //CAUTION! lose ICC info for CMYK images
			//oIn.close();
			BufferedImage original = toBufferedImage(oIn);
			
			int origH = original.getHeight();
			int origW = original.getWidth();
			int imageWidth = 0, imageHeight = 0;

			if (keepRatio) {
				if (height<=0) {
					if (origW<=width) { // 比原图还大, copy
						oIn = oFile.getInputStream();
						newSf = getStorage().createFileWithName(oFile.getPath(), name + "_" + width + "w" + fileExt);
						copyStream(oIn, newSf.getOutputStream());
						imageWidth = origW;
						imageHeight = origH;
					} else {
						newSf = getStorage().createFileWithName(oFile.getPath(), name + "_" + width + "w" + fileExt);
						OutputStream out = newSf.getOutputStream();
						Thumbnails.of(original)
						.width(width)
						.keepAspectRatio(true)
						.outputFormat(fileExt.substring(1))
						.toOutputStream(out);
						out.close();
						imageWidth = width;
						imageHeight = Math.round((float)width/origW * origH);
					}
				} else if (width<=0) {
					if (origH<=height) { // 比原图还大, copy
						oIn = oFile.getInputStream();
						newSf = getStorage().createFileWithName(oFile.getPath(), name + "_" + height + "h" + fileExt);
						copyStream(oIn, newSf.getOutputStream());
						imageWidth = origW;
						imageHeight = origH;
					} else {
						newSf = getStorage().createFileWithName(oFile.getPath(), name + "_" + height + "h" + fileExt);
						OutputStream out = newSf.getOutputStream();
						Thumbnails.of(original)
						.height(height)
						.keepAspectRatio(true)
						.outputFormat(fileExt.substring(1))
						.toOutputStream(out);
						out.close();
						imageWidth = Math.round((float)height/origH * origW);
						imageHeight = height;
					}
				} else {
					newSf = getStorage().createFileWithName(oFile.getPath(), name + "_" + width + "x" + height + fileExt);
					OutputStream out = newSf.getOutputStream();
					Thumbnails.of(original)
					.size(width, height)
					.keepAspectRatio(true)
					.outputFormat(fileExt.substring(1))
					.toOutputStream(out);
					out.close();
					imageWidth = width;
					imageHeight = height;
				}
			} else if (cutCenter) { //截取中间区域
				if (origH<height)
					throw new IllegalSizeException("图片高度必须大于"+height);
				if (origW<width)
					throw new IllegalSizeException("图片宽度必须大于"+width);
				
				int x = 0, y =0;
				int sourceW = origW, sourceH = origH;
				float ratio = (float)width/height;
				float oldRatio = (float)origW/origH;
				if (ratio>=oldRatio) {
					sourceH = Math.round((float)origW/width*height);
					y = Math.round(origH/2 - (height/2) * ((float)origW/width)); // 取景框放大到宽与原图一样
				} else if (ratio<oldRatio){
					sourceW = Math.round((float)origH/height*width);
					x = Math.round(origW/2 - (width/2) * ((float)origH/height)); // 取景框放大到高与原图一样
				}
				newSf = getStorage().createFileWithName(oFile.getPath(), name + "_" + width + "x" + height + fileExt);
				OutputStream out = newSf.getOutputStream();
				
				Thumbnails.of(original)
				.sourceRegion(x, y, sourceW, sourceH)
				.size(width, height)
				.keepAspectRatio(false)
				.outputFormat(fileExt.substring(1))
				.toOutputStream(out);
				out.close();
				imageWidth = width;
				imageHeight = height;
			} else {
				imageWidth = width;
				imageHeight = (height <= 0 ? origH : height);
			
				newSf = getStorage().createFileWithName(oFile.getPath(), name + "_" + imageWidth + "x" + imageHeight + fileExt);
				OutputStream out = newSf.getOutputStream();
				
				Thumbnails.of(original)
				.forceSize(imageWidth, imageHeight)
				.outputFormat(fileExt.substring(1))
				.toOutputStream(out);
				out.close();
			}
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("image_url", newSf.getFullName());
			params.put("image_width", imageWidth);
			params.put("image_height", imageHeight);
			return params;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static List<ImageInfo> getImagesToSave(String prefix, Map<String, String[]> params) {
		Map<String, ImageInfo> infos = new HashMap<String, ImageInfo>();
		for (String name: params.keySet()) {
			if (name.startsWith(prefix)) { // img_2014xxxxx_w
				String iname = null;
				if (name.endsWith("h") || name.endsWith("w") || name.endsWith("f"))
					iname = name.substring(name.indexOf("_") + 1, name.lastIndexOf("_"));
				else
					iname = name.substring(name.indexOf("_") + 1);
				ImageInfo info = infos.get(iname);
				if (info == null) {
					info = new ImageInfo();
					info.name = iname;
					infos.put(iname, info);
				}
				if (name.endsWith("w")) {
					info.width = Integer.parseInt(params.get(name)[0]);
				} else if (name.endsWith("h")) {
					info.height = Integer.parseInt(params.get(name)[0]);
				} else {
					info.url = params.get(name)[0];
				}
			}
		}
		return new ArrayList<ImageInfo>(infos.values());
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param imageName 文件扩展名.被_替代
	 * @param imageFolder
	 * @return image_url, w, h
	 */
	public static Map<String, Object> crop(int x, int y, int w, int h, String imageFolder, String imageName) {
		if (!useImageMagick)
			return crop1(x, y, w, h, imageFolder, imageName);
		
		int splitIndex = imageName.lastIndexOf("_"); // 文件扩展名.被_替代
		String imageExt = "." + imageName.substring(splitIndex+1);
		String imageNameNoExt = imageName.substring(0, splitIndex);
		String imageNewName = imageNameNoExt + "_crop" + imageExt;
		imageName = imageNameNoExt + imageExt;
		MagickImage original = null;
		try {
			StorageFile sf = getStorage().getFile(imageFolder + imageName);
			StorageFile newSf = getStorage().createFileWithName(imageFolder, imageNewName);
			OutputStream out = newSf.getOutputStream();
			
			original = getMagickImage(sf.getInputStream());
			MagickImage newImg = original.cropImage(new Rectangle(x,y,w,h));
			String tempFile = TEMP_DIR + System.nanoTime();
			newImg.setFileName(tempFile);
			newImg.writeImage(new magick.ImageInfo());
			
			copyStream(new FileInputStream(tempFile), out);
			new File(tempFile).delete();
			
			out.close();
			getStorage().deleteFile(sf.getFullName());
		} catch (Exception e) {
			logger.error("图片裁切失败",e); 
		} finally {
			if (original!=null)
				original.destroyImages();
		}

		Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("imageUrl", imageFolder + imageNewName);
        ret.put("w", w);
        ret.put("h", h);
        return ret;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param imageName 文件扩展名.被_替代
	 * @param imageFolder
	 * @return image_url, w, h
	 */
	private static Map<String, Object> crop1(int x, int y, int w, int h, String imageFolder, String imageName) {
		int splitIndex = imageName.lastIndexOf("_"); // 文件扩展名.被_替代
		String imageExt = "." + imageName.substring(splitIndex+1);
		String imageNameNoExt = imageName.substring(0, splitIndex);
		String imageNewName = imageNameNoExt + "_crop" + imageExt;
		imageName = imageNameNoExt + imageExt;
		
		try {
			StorageFile sf = getStorage().getFile(imageFolder + imageName);
			StorageFile newSf = getStorage().createFileWithName(imageFolder, imageNewName);
			OutputStream out = newSf.getOutputStream();
			BufferedImage bi = toBufferedImage(sf.getInputStream());
			Thumbnails.of(bi)
			.sourceRegion(x, y, w, h)
			.size(w, h)
			.keepAspectRatio(false)
			.outputFormat(imageExt.substring(1))
			.toOutputStream(out);
			out.close();
			getStorage().deleteFile(sf.getFullName());
		} catch (Exception e) {
			logger.error("图片裁切失败",e); 
		}

		Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("imageUrl", imageFolder + imageNewName);
        ret.put("w", w);
        ret.put("h", h);
        return ret;
	}
	
	/**
	 * 
	 * @param imageFile
	 * @param imageFileContentType
	 * @param imageFileFileName
	 * @param imageFolder
	 * @param needLimitWidth
	 * @return imageUrl, w, h
	 */
	public static Map<String, Object> upload(File imageFile, String imageFileContentType, String imageFileFileName,
			String imageFolder, boolean needLimitMinimumWidth, boolean needLimitMaximumWidth) {
		if (!useImageMagick)
			return upload1(imageFile, imageFileContentType, imageFileFileName, imageFolder, needLimitMinimumWidth, needLimitMaximumWidth);
		
		if (imageFile==null)
			return null;
		//控制图片类型  
        if(imageFileContentType.equals("image/gif") || imageFileContentType.equals("image/jpeg") ||   
        		imageFileContentType.equals("image/png") || imageFileContentType.equals("image/bmp"))  
        {
        	StorageFile storageFile = null;
        	OutputStream out = null;
        	int finalImgWidth = 0;
        	int finalImgHeight = 0;
        	MagickImage original = null;
        	try {
        		storageFile = getStorage().createFile(imageFolder,imageFileContentType,imageFileFileName.substring(imageFileFileName.lastIndexOf('.')));
        		out = storageFile.getOutputStream();
        		
        		original = new MagickImage(new magick.ImageInfo(imageFile.getAbsolutePath()));
        		// 处理图片大小问题     359或以下不准上传并提示错误，360-540保持不变，541或以上统一变成540 （以上为图片宽数值）
				int origWidth = original.getDimension().width;
				int origHeight = original.getDimension().height;
				if(needLimitMinimumWidth) {
					if(359 >= origWidth) {
						throw new IllegalSizeException("图片宽度必须大于359！");
					}
				}
				if(needLimitMaximumWidth) {
					if(540 < origWidth) {
						finalImgWidth = 540;
						finalImgHeight = Math.round((float)540/origWidth * origHeight);		// 获取等比例缩放后的height
						MagickImage newImg = original.scaleImage(finalImgWidth, finalImgHeight);
						String tempFile = TEMP_DIR + System.nanoTime();
						newImg.setFileName(tempFile);
						newImg.writeImage(new magick.ImageInfo());
						
						copyStream(new FileInputStream(tempFile), out);
						new File(tempFile).delete();
					}else {
						FileUtils.copyFile(imageFile, out);
						finalImgWidth = origWidth;
						finalImgHeight = origHeight;
					}
				}else {
					FileUtils.copyFile(imageFile, out);
					finalImgWidth = origWidth;
					finalImgHeight = origHeight;
				}
			} catch (Exception ioe) {
				logger.error("图片上传失败！", ioe);
			} finally {
				try {
					out.close();
				} catch (Exception e) {
					logger.error("输出流关闭失败！", e);
				}
				if (original!=null)
					original.destroyImages();
			}
        	Map<String, Object> ret = new HashMap<String, Object>();
        	ret.put("imageUrl", storageFile.getFullName());
        	ret.put("w", finalImgWidth);
        	ret.put("h", finalImgHeight);
        	return ret;
        } else {
        	return null;
        }
	}
	
	/**
	 * 
	 * @param imageFile
	 * @param imageFileContentType
	 * @param imageFileFileName
	 * @param imageFolder
	 * @return imageUrl, w, h
	 */
	private static Map<String, Object> upload1(File imageFile, String imageFileContentType, String imageFileFileName, 
			String imageFolder, boolean needLimitMinimumWidth, boolean needLimitMaximumWidth) {
		if (imageFile==null)
			return null;
		//控制图片类型  
        if(imageFileContentType.equals("image/gif") || imageFileContentType.equals("image/jpeg") ||   
        		imageFileContentType.equals("image/png") || imageFileContentType.equals("image/bmp"))  
        {
        	StorageFile storageFile = null;
        	OutputStream out = null;
        	int finalImgWidth = 0;
        	int finalImgHeight = 0;
        	try {
        		storageFile = getStorage().createFile(imageFolder,imageFileContentType,imageFileFileName.substring(imageFileFileName.lastIndexOf('.')));
        		out = storageFile.getOutputStream();
        		
        		// 处理图片大小问题     359或以下不准上传并提示错误，360-540保持不变，541或以上统一变成540 （以上为图片宽数值）
				BufferedImage buffImg = toBufferedImage(new FileInputStream(imageFile));
				int origWidth = buffImg.getWidth();
				int origHeight = buffImg.getHeight();
				if(needLimitMinimumWidth) {
					if(359 >= origWidth) {
						throw new IllegalSizeException("图片宽度必须大于359！");
					}
				}
				if(needLimitMaximumWidth) {
					if(541 <= origWidth) {
						Thumbnails.of(buffImg).width(540).keepAspectRatio(true).outputFormat(imageFileFileName.substring(imageFileFileName.indexOf(".")+1)).toOutputStream(out);
						finalImgWidth = 540;
						finalImgHeight = Math.round((float)540/origWidth * origHeight);		// 获取等比例缩放后的height
					}else {
						FileUtils.copyFile(imageFile, out);
						finalImgWidth = origWidth;
						finalImgHeight = origHeight;
					} 
				}else {
					FileUtils.copyFile(imageFile, out);
					finalImgWidth = origWidth;
					finalImgHeight = origHeight;
				}
			} catch (IOException ioe) {
				logger.error("图片上传失败！", ioe);
			} finally {
				try {
					out.close();
				} catch (Exception e) {
					logger.error("输出流关闭失败！", e);
				}
			}
        	Map<String, Object> ret = new HashMap<String, Object>();
        	ret.put("imageUrl", storageFile.getFullName());
        	ret.put("w", finalImgWidth);
        	ret.put("h", finalImgHeight);
        	return ret;
        } else {
        	return null;
        }
	}
	
	/**
	 * 返回图片服务器的图片前缀地址。
	 * 
	 * @return
	 */
	public static String getWebImagePathPrefix() {
		return "http://192.168.1.230:88";
	}

	/**
	 * 删除服务器上的图片
	 * @author weiding.huang
	 * @param bannerImagePath
	 */
	public static boolean delete(String bannerImagePath) {
		try {
			getStorage().deleteFile(bannerImagePath);
			return true;
		} catch (IOException e) {
			logger.error("image:"+bannerImagePath+" delete failed!");
			return false;
		}
	}
	
	/** This method returns true if the specified image has transparent pixels  */
    private static boolean hasAlpha(Image image) {  
            // If buffered image, the color model is readily available  
      if (image instanceof BufferedImage) {  
        BufferedImage bimage = (BufferedImage)image;  
        return bimage.getColorModel().hasAlpha();  
      }  
      
      // Use a pixel grabber to retrieve the image's color model;  
      // grabbing a single pixel is usually sufficient  
      PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);  
      try {  
         pg.grabPixels();  
      } catch (InterruptedException e) { }  
      
      // Get the image's color model  
      ColorModel cm = pg.getColorModel();  
      return cm.hasAlpha();  
    }  
  
    public static BufferedImage toBufferedImage(InputStream in) throws FileNotFoundException {
    	File tmp = new File(TEMP_DIR + System.nanoTime());
    	copyStream(in, new FileOutputStream(tmp));
    	Image img = Toolkit.getDefaultToolkit().getImage(tmp.getAbsolutePath());
    	BufferedImage ret = toBufferedImage(img);
    	tmp.delete();
    	return ret;
    }
    
    /** This method returns a buffered image with the contents of an image  */
    private static BufferedImage toBufferedImage(Image image) {  
        if (image instanceof BufferedImage) {  
            return (BufferedImage)image;  
        }  
      
        // This code ensures that all the pixels in the image are loaded  
        image = new ImageIcon(image).getImage();  
      
        // Determine if the image has transparent pixels; for this method's  
        // implementation, see e661 Determining If an Image Has Transparent Pixels  
        boolean hasAlpha = hasAlpha(image);  
      
        // Create a buffered image with a format that's compatible with the screen  
        BufferedImage bimage = null;  
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();  
        try {  
            // Determine the type of transparency of the new buffered image  
            int transparency = Transparency.OPAQUE;  
            if (hasAlpha) {  
                transparency = Transparency.BITMASK;  
            }  
      
            // Create the buffered image  
            GraphicsDevice gs = ge.getDefaultScreenDevice();  
            GraphicsConfiguration gc = gs.getDefaultConfiguration();  
            bimage = gc.createCompatibleImage(  
                image.getWidth(null), image.getHeight(null), transparency);  
        } catch (HeadlessException e) {  
            // The system does not have a screen  
        }  
      
        if (bimage == null) {  
            // Create a buffered image using the default color model  
            int type = BufferedImage.TYPE_INT_RGB;  
            if (hasAlpha) {  
                type = BufferedImage.TYPE_INT_ARGB;  
            }  
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);  
        }  
      
        // Copy image to buffered image  
        Graphics g = bimage.createGraphics();  
      
        // Paint the image onto the buffered image  
        g.drawImage(image, 0, 0, null);  
        g.dispose();  
      
        return bimage;  
    }
    
    /**
     * get image height and width
     * @param imageFile
     * @return	map => {"height":h, "width":w}
     */
    public static Map<String, Integer> getImageHeightAndWidth(StorageFile imageFile) {
    	Map<String, Integer> result = new HashMap<String, Integer>(2);
    	try {
			InputStream in = imageFile.getInputStream();
			BufferedImage image = toBufferedImage(in);
			result.put("height", image.getHeight());
			result.put("width", image.getWidth());
		} catch (Exception e) {
			logger.error("getImageHeightAndWidth failed!");
		}
    	return result;
    }
}
