package com.logmonitor.filemonitor.scanner;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import com.logmonitor.filemonitor.config.Conf;

public class FileScanner {
	private Conf.ConfItem confItem = null;
	private FileAlterationObserver observer = null;
	private FileListener fileListener = null;
	private FileAlterationMonitor monitor = null;
	
	public FileScanner(Conf.ConfItem confItem, FileListener fileListener) {
		this.confItem = confItem;
		this.fileListener = fileListener;
		this.reigsterListener();
	}
	
	private void reigsterListener() {
		if (confItem.isUseLogNameFilter()) {
			this.observer = new FileAlterationObserver(confItem.getLogPath(), 
					FileFilterUtils.suffixFileFilter(confItem.getLogNameFilter()));
		} else {
			this.observer = new FileAlterationObserver(confItem.getLogPath());
		}
		this.observer.addListener(this.fileListener);
		this.monitor = new FileAlterationMonitor(confItem.getScanInterval(), this.observer);
	}
	
	public void startScan() {
		try {
			this.monitor.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stopScan() {
		try {
			Iterable<FileAlterationObserver> observers = this.monitor.getObservers();
			for (FileAlterationObserver _observer : observers) {
				Iterable<FileAlterationListener> listeners = _observer.getListeners();
				for (FileAlterationListener listener : listeners) {
					FileListener fileListener = (FileListener)listener;
					fileListener.executeStop(_observer);
				}
			}
			this.monitor.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
