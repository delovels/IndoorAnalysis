package com.bupt.indoorPosition.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import com.bupt.indoorPosition.uti.Constants;
import com.bupt.indoorPosition.uti.HttpUtil;
import com.bupt.indoorpostion.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class CheckVersionTask {
	private Context context;
	private Map<String, Object> response;

	public CheckVersionTask(Context ctx) {
		this.context = ctx;

	}

	public void run() {
		new Thread() {
			@Override
			public void run() {

				try {
					Map<String, String> version = new HashMap<String, String>();
					int versioncode = getVersionCode(context);
					Log.i("apk update version", "" + versioncode);
					version.put("version", "" + versioncode);
					String url = context.getString(R.string.hostUrl)
							+ "/location/check";
					response = HttpUtil.post(url, version);
					Log.d("response", response.get("isUpdate").toString());
					if (response.get("isUpdate").toString().equals("" + 0)) {
						Message msg = new Message();
						msg.what = Constants.MSG.NOT_UPDATA;
						handler.sendMessage(msg);
						Log.d("不需要更新", response.get("isUpdate").toString());
						return;
						// LoginMain();
					} else {
						Log.d("TAG", "版本号不同 ,提示用户升级 ");
						Message msg = new Message();
						msg.what = Constants.MSG.UPDATA_CLIENT;
						Bundle b = new Bundle();
						b.putString("description", response.get("description")
								.toString());
						msg.setData(b);
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					// 待处理
					Message msg = new Message();
					msg.what = Constants.MSG.GET_UNDATAINFO_ERROR;
					handler.sendMessage(msg);
					e.printStackTrace();
				}
			}
		}.start();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.MSG.UPDATA_CLIENT:
				// 对话框通知用户升级程序
				Bundle b = msg.getData();
				showUpdataDialog(b.getString("description"));
				break;
			case Constants.MSG.NOT_UPDATA:
				// 不需要升级程序
				Toast.makeText(context, "版本最新无需升级", 1000).show();
				break;
			case Constants.MSG.GET_UNDATAINFO_ERROR:
				// 服务器超时
				Toast.makeText(context, "获取服务器更新信息失败", 1000).show();
				// LoginMain();
				break;
			case Constants.MSG.DOWN_ERROR:
				// 下载apk失败
				Toast.makeText(context, "下载新版本失败", 1000).show();
				// LoginMain();
				break;
			}
		}
	};

	/*
	 * 
	 * 弹出对话框通知用户更新程序
	 * 
	 * 弹出对话框的步骤： 1.创建alertDialog的builder. 2.要给builder设置属性, 对话框的内容,样式,按钮
	 * 3.通过builder 创建一个对话框 4.对话框show()出来
	 */
	protected void showUpdataDialog(String message) {
		AlertDialog.Builder builer = new Builder(context);
		builer.setTitle("版本升级");
		builer.setMessage("是否进行版本升级 \n新版本特性：\n" + message);
		// 当点确定按钮时从服务器上下载 新的apk 然后安装
		builer.setPositiveButton("确定", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i("TAG", "下载apk,更新");

				downLoadApk();
			}
		});
		// 当点取消按钮时进行登录
		builer.setNegativeButton("取消", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				return;
			}
		});
		AlertDialog dialog = builer.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	/*
	 * 从服务器中下载APK
	 */
	protected void downLoadApk() {
		final ProgressDialog pd; // 进度条对话框
		pd = new ProgressDialog(context);
		pd.setCanceledOnTouchOutside(false);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在下载更新");
		pd.show();
		new Thread() {
			@Override
			public void run() {
				try {
					// response.put("url","http://115.28.164.238/App/1.0.4.apk");
					// Log.d("response.get(url)",(String)response.get("url"));
					File file = getFileFromServer(
							"http://115.28.164.238/App/androidServer" + ".apk",
							pd);
					Log.i("check version ",
							"downLoadApk file " + file == null ? "file null"
									: file.getPath());
					sleep(3000);
					installApk(file);

				} catch (Exception e) {
					Message msg = new Message();
					msg.what = Constants.MSG.DOWN_ERROR;
					handler.sendMessage(msg);
					e.printStackTrace();
				} finally {
					pd.dismiss(); // 结束掉进度条对话框
				}
			}
		}.start();
	}

	// 安装apk
	protected void installApk(File file) {
		Intent intent = new Intent();
		// 执行动作
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		// 执行的数据类型
		// intent.setDataAndType(Uri.fromFile(file),// Uri.parse("file://" +
		// // file.toString()),
		// "application/vnd.Android.package-archive");
		intent.setDataAndType(Uri.parse("file://" + file.toString()),
				"application/vnd.android.package-archive");

		context.startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid());// 如果不加上这句的话在apk安装完成之后点击单开会崩溃
	}

	// public void uninstallAPK() {
	// Uri packageURI = Uri.parse("package:com.bupt.indoorpostion");
	// Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
	// context.startActivity(uninstallIntent);
	//
	// }

	// /*
	// * 进入程序的主界面
	// */
	// private void LoginMain() {
	// Intent intent = new Intent(this, MainActivity.class);
	// startActivity(intent);
	// // 结束掉当前的activity
	// this.finish();
	// }
	public File getFileFromServer(String path, ProgressDialog pd)
			throws Exception {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			URL url = new URL(path);
			// HttpURLConnection conn = (HttpURLConnection)
			// url.openConnection();
			URLConnection conn = (URLConnection) url.openConnection();
			conn.connect();
			conn.setConnectTimeout(5000);
			// 获取到文件的大小
			pd.setMax(conn.getContentLength());
			Log.d("response.get(url)", "" + conn.getContentLength());
			InputStream is = conn.getInputStream();
			// File file = new File(Environment.getExternalStorageDirectory(),
			// "update" + response.get("newVersion") + ".apk");

			File file = new File(Environment.getExternalStorageDirectory(),
					"update" + response.get("newVersion") + ".apk");// context.getApplicationContext().getFilesDir()

			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				total += len;
				// 获取当前下载量
				pd.setProgress(total);
			}
			fos.close();
			bis.close();
			is.close();
			return file;
		} else {
			return null;
		}
	}

	public static int getVersionCode(Context context)// 获取版本号(内部识别号)
	{
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
}