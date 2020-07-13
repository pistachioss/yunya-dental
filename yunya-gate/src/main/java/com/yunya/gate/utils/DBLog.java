package com.yunya.gate.utils;

import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.domain.LogInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 系统操作日志处理
 *
 * @author wanghaobin
 * @create 2017-07-01 15:28
 */
@Slf4j
public class DBLog extends Thread {

  /** 注入对象 */
  private RemoteSystemServiceFeign systemServiceFeign;

  private static DBLog dblog = null;

  private static final BlockingQueue<LogInfo> LOG_INFO_QUEUE =
      new LinkedBlockingQueue<LogInfo>(1024);

  public RemoteSystemServiceFeign getLogService() {
    return systemServiceFeign;
  }

  public DBLog setLogService(RemoteSystemServiceFeign systemServiceFeign) {
    if (this.systemServiceFeign == null) {
      this.systemServiceFeign = systemServiceFeign;
    }
    return this;
  }

  /**
   * 使用单例创建对象
   *
   * @return
   */
  public static synchronized DBLog getInstance() {
    if (dblog == null) {
      dblog = new DBLog();
    }
    return dblog;
  }

  private DBLog() {
    super("CLogOracleWriterThread");
  }

  /**
   * 通知队列
   *
   * @param logInfo
   */
  public void offerQueue(LogInfo logInfo) {
    try {
      LOG_INFO_QUEUE.offer(logInfo);
    } catch (Exception e) {
      log.error("日志写入失败", e);
    }
  }

  @Override
  public void run() {
    // 缓冲队列
    List<LogInfo> bufferedLogList = new ArrayList<LogInfo>();
    while (true) {
      try {
        bufferedLogList.add(LOG_INFO_QUEUE.take());
        LOG_INFO_QUEUE.drainTo(bufferedLogList);
        if (bufferedLogList.size() > 0) {
          // 写入日志
          for (LogInfo log : bufferedLogList) {
            systemServiceFeign.saveLog(log);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        // 防止缓冲队列填充数据出现异常时不断刷屏
        try {
          Thread.sleep(1000);
        } catch (Exception ignored) {
        }
      } finally {
        if (bufferedLogList.size() > 0) {
          try {
            bufferedLogList.clear();
          } catch (Exception ignored) {
          }
        }
      }
    }
  }
}
