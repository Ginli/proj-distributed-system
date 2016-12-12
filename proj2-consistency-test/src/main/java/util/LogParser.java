package util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import util.exceptions.*;

/**
 * Created by Hongji Li on Dec 12 2016.
 * */

public class LogParser {
	
	private static final String fileNameFormat = "client_";
	
	private final FileSystem fileSystem;
	
	public LogParser() {
		fileSystem = FileSystems.getDefault();
	}
	
	public List<OperationInterval> startParsingLogs(String path, int logNum) throws IOException, ConnectionErrorException {
		if (path == null)
			return new LinkedList<OperationInterval>();
		List<OperationInterval> parsedResult = new LinkedList<>();
		try {
			for (int i = 0; i < logNum; ++i) {
				parseLogFile(path, i, parsedResult);
			}
		} catch (ParseException pe) {
			System.out.printf("Problem when parsing date in log files: %s%n", pe.getMessage());
		}
		return parsedResult;
	}
	
	private void parseLogFile(String path, int index, List<OperationInterval> into) throws IOException, ParseException, ConnectionErrorException {
		Path filePath = fileSystem.getPath(path, String.format("%s%d.log", fileNameFormat, index));
		List<String> lines = Files.readAllLines(filePath, Charset.forName("UTF-8"));
		Map<Long, OperationInterval> map = new HashMap<>(lines.size());
		for (String line : lines) {
			String[] result = fetchInfo(line);//array {time, clientIndex, cmdIndex, op, timeType, key, value}
			if (result == null || result.length < 1)
				continue;
			long timeInMilli = convertDate(result[0]);
			Long cmdIndex = Long.valueOf(result[2]);
			if (!map.containsKey(cmdIndex)) {
				OperationInterval interval = new OperationInterval(result[3], Long.parseLong(result[1]), cmdIndex.longValue());
				map.put(cmdIndex, interval);
			}
			OperationInterval interval = map.get(cmdIndex);
			if ("start".equals(result[4]))
				interval.setStartTime(timeInMilli);
			if ("finish".equals(result[4]))
				interval.setEndTime(timeInMilli);
			if (result[5] != null && result[5].length() > 0)
				interval.setKey(result[5]);
			if (result[6] != null && result[6].length() > 0)
				interval.setValue(result[6]);
		}
		for (OperationInterval interval : map.values()) {
			into.add(interval);
		}
		map.clear();
	}
	
	private String[] fetchInfo(String original) throws ConnectionErrorException {
		//return array {time, clientIndex, cmdIndex, op, timeType, key, value}
		if (original == null || original.length() == 0)
			return null;
		original = original.trim();
		int beginIdx = 0, endIdx = 0;
		String[] result = new String[7];
		
		try {
			endIdx += 23;
			result[0] = original.substring(beginIdx, endIdx);//date
			beginIdx = endIdx;
			
			beginIdx = original.indexOf("client_", beginIdx);
			beginIdx += "client_".length();
			endIdx = original.indexOf(':', beginIdx);
			result[1] = original.substring(beginIdx, endIdx);//client index
			beginIdx = endIdx;
			
			beginIdx = original.indexOf("cmdIndex=", beginIdx);
			beginIdx += "cmdIndex=".length();
			endIdx = original.indexOf(',', beginIdx);
			result[2] = original.substring(beginIdx, endIdx);//cmd index
			beginIdx = endIdx;
			
			beginIdx = original.indexOf("cmd=", beginIdx);
			beginIdx += "cmd=".length();
			endIdx = original.indexOf(',', beginIdx);
			result[3] = original.substring(beginIdx, endIdx);//command type
			beginIdx = endIdx;
			
			beginIdx = original.indexOf("timeType=", beginIdx);
			beginIdx += "timeType=".length();
			endIdx = original.indexOf(',', beginIdx);
			result[4] = original.substring(beginIdx, endIdx);//time type
			beginIdx = endIdx;
			
			beginIdx = original.indexOf("key=", beginIdx);
			beginIdx += "key=".length();
			endIdx = original.indexOf(',', beginIdx);
			result[5] = original.substring(beginIdx, endIdx == -1 ? original.length() : endIdx);//key
			beginIdx = endIdx;
			
			if (beginIdx != -1) {
				beginIdx = original.indexOf("val=", beginIdx);
				beginIdx += "val=".length();
				endIdx = original.length();
				result[6] = original.substring(beginIdx, endIdx);//value
				beginIdx = endIdx;
			}
		} catch (IndexOutOfBoundsException e) {
//			System.out.printf("Log information format wrong. Maybe server returned error. %s%n", e.getMessage());
			throw new ConnectionErrorException("Server connection error.");
		}
		return result;
	}
	
	private long convertDate(String date) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		return format.parse(date).getTime();
	}

}
