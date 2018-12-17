/**
 * 
 */
package cn.miao.framework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * only for windows
 * 
 * @author nealmiao
 *
 */
public class Killer {

	public static void main(String[] args) throws IOException {
		System.out.println(getProcesses());
		List<Long> rtList = Killer.isRunning("360chrome.exe");
		for (Long pid : rtList) {
			System.out.println(pid);
			killById(pid);
		}
	}

	public static void killById(long pid) {
		Process proc = null;
		try {
			proc = Runtime.getRuntime().exec("taskkill /f /pid " + pid);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			String info = br.readLine();
			while (info != null) {
				System.out.println(info);
				info = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			proc.destroy();
		}
	}

	/**
	 * 
	 * @������ ��isRunning<br>
	 * @�������� ���ж�ϵͳ�����Ƿ����<br>
	 * @����ʱ�� ��2014-3-5����11:25:46 <br>
	 * @param exeName
	 *            ��������
	 * @return �������� ��long
	 */
	public static List<Long> isRunning(String exeName) {
		Process proc = null;
		List<Long> rtList = new ArrayList<Long>();
		try {
			proc = Runtime.getRuntime().exec("tasklist");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			String info = br.readLine();
			int s_pid = 0, e_pid = 0;
			while (info != null) {
				// System.out.println(info);
				if (info.contains("==")) {
					String[] tmps = info.split(" ");
					s_pid = tmps[0].length() + 1;
					e_pid = s_pid + tmps[1].length();
				}
				if (info.indexOf(exeName) >= 0 && s_pid > 0) {
					String pid = info.substring(s_pid, e_pid).trim();
					rtList.add(Long.parseLong(pid));
				}
				info = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			proc.destroy();
		}
		return rtList;
	}
	
	public static String getProcesses() {
		Process proc = null;
		String rtString = "";
		try {
			proc = Runtime.getRuntime().exec("tasklist");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			String info = br.readLine();
			while (info != null) {
				rtString += info + "\n";
				info = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			proc.destroy();
		}
		return rtString;
	}

}
