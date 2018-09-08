package com.lensim.fingerchat.fingerchat.model.result;

import com.lensim.fingerchat.commons.base.BaseResponse;
import com.lensim.fingerchat.fingerchat.model.bean.WorkCenterBean;

public class PersimmonResourceTreeResult extends BaseResponse<PersimmonResourceTreeResult.Data> {

    public static class Data {
        private FxConsoleBean fxConsole;
        private FxClientBean fxClient;
        private FxServerBean fxServer;

        public FxConsoleBean getFxConsole() {
            return fxConsole;
        }

        public void setFxConsole(FxConsoleBean fxConsole) {
            this.fxConsole = fxConsole;
        }

        public FxClientBean getFxClient() {
            return fxClient;
        }

        public void setFxClient(FxClientBean fxClient) {
            this.fxClient = fxClient;
        }

        public FxServerBean getFxServer() {
            return fxServer;
        }

        public void setFxServer(FxServerBean fxServer) {
            this.fxServer = fxServer;
        }

        public static class FxConsoleBean {
            /**
             * fxgroupUser : {"userInfo":{"uInfo":"用户信息","thisNode":"飞鸽助手"},"thisNode":"用户用户群","userGroup":{"thisNode":"用户群","summaryList":"用户群组","groupMember":"群成员"},"users":{"userList":"用户好友列表","thisNode":"用户","digest":"用户摘要信息"}}
             * monitorConsole : {"showData":{"allDevicePage":"查看所有拓扑","thisNode":"查看数据","myMonitorInfo":"我的监控项","updateMyDevicePage":"修改拓扑图","updateMyMonitorInfo":"修改监控项","myDevicePage":"我的拓扑图","mySetting":"个人设置","checkAllInfos":"查看详细数据"},"thisNode":"服务监控"}
             * fxsession : 会话
             * thisNode : 飞鸽总控
             * assistConsole : {"monitoringManager":{"taskMonitoring":"任务监控","thisNode":"监控管理","executorMonitoring":"线程监控"},"thisNode":"飞鸽小秘书","interceptManager":{"userIntercept":"用户拦截","thisNode":"拦截管理","blacklistIntercep":"黑名单拦截"},"messageManager":{"ruleInterceptMsg":"规则拦截消息","messagePushList":"消息推送列表","thisNode":"消息管理","blacklistMsg":"黑名单消息"}}
             */

            private FxgroupUserBean fxgroupUser;
            private MonitorConsoleBean monitorConsole;
            private String fxsession;
            private String thisNode;
            private AssistConsoleBean assistConsole;

            public FxgroupUserBean getFxgroupUser() {
                return fxgroupUser;
            }

            public void setFxgroupUser(FxgroupUserBean fxgroupUser) {
                this.fxgroupUser = fxgroupUser;
            }

            public MonitorConsoleBean getMonitorConsole() {
                return monitorConsole;
            }

            public void setMonitorConsole(MonitorConsoleBean monitorConsole) {
                this.monitorConsole = monitorConsole;
            }

            public String getFxsession() {
                return fxsession;
            }

            public void setFxsession(String fxsession) {
                this.fxsession = fxsession;
            }

            public String getThisNode() {
                return thisNode;
            }

            public void setThisNode(String thisNode) {
                this.thisNode = thisNode;
            }

            public AssistConsoleBean getAssistConsole() {
                return assistConsole;
            }

            public void setAssistConsole(AssistConsoleBean assistConsole) {
                this.assistConsole = assistConsole;
            }

            public static class FxgroupUserBean {
                /**
                 * userInfo : {"uInfo":"用户信息","thisNode":"飞鸽助手"}
                 * thisNode : 用户用户群
                 * userGroup : {"thisNode":"用户群","summaryList":"用户群组","groupMember":"群成员"}
                 * users : {"userList":"用户好友列表","thisNode":"用户","digest":"用户摘要信息"}
                 */

                private UserInfoBean userInfo;
                private String thisNode;
                private UserGroupBean userGroup;
                private UsersBean users;

                public UserInfoBean getUserInfo() {
                    return userInfo;
                }

                public void setUserInfo(UserInfoBean userInfo) {
                    this.userInfo = userInfo;
                }

                public String getThisNode() {
                    return thisNode;
                }

                public void setThisNode(String thisNode) {
                    this.thisNode = thisNode;
                }

                public UserGroupBean getUserGroup() {
                    return userGroup;
                }

                public void setUserGroup(UserGroupBean userGroup) {
                    this.userGroup = userGroup;
                }

                public UsersBean getUsers() {
                    return users;
                }

                public void setUsers(UsersBean users) {
                    this.users = users;
                }

                public static class UserInfoBean {
                    /**
                     * uInfo : 用户信息
                     * thisNode : 飞鸽助手
                     */

                    private String uInfo;
                    private String thisNode;

                    public String getUInfo() {
                        return uInfo;
                    }

                    public void setUInfo(String uInfo) {
                        this.uInfo = uInfo;
                    }

                    public String getThisNode() {
                        return thisNode;
                    }

                    public void setThisNode(String thisNode) {
                        this.thisNode = thisNode;
                    }
                }

                public static class UserGroupBean {
                    /**
                     * thisNode : 用户群
                     * summaryList : 用户群组
                     * groupMember : 群成员
                     */

                    private String thisNode;
                    private String summaryList;
                    private String groupMember;

                    public String getThisNode() {
                        return thisNode;
                    }

                    public void setThisNode(String thisNode) {
                        this.thisNode = thisNode;
                    }

                    public String getSummaryList() {
                        return summaryList;
                    }

                    public void setSummaryList(String summaryList) {
                        this.summaryList = summaryList;
                    }

                    public String getGroupMember() {
                        return groupMember;
                    }

                    public void setGroupMember(String groupMember) {
                        this.groupMember = groupMember;
                    }
                }

                public static class UsersBean {
                    /**
                     * userList : 用户好友列表
                     * thisNode : 用户
                     * digest : 用户摘要信息
                     */

                    private String userList;
                    private String thisNode;
                    private String digest;

                    public String getUserList() {
                        return userList;
                    }

                    public void setUserList(String userList) {
                        this.userList = userList;
                    }

                    public String getThisNode() {
                        return thisNode;
                    }

                    public void setThisNode(String thisNode) {
                        this.thisNode = thisNode;
                    }

                    public String getDigest() {
                        return digest;
                    }

                    public void setDigest(String digest) {
                        this.digest = digest;
                    }
                }
            }

            public static class MonitorConsoleBean {
                /**
                 * showData : {"allDevicePage":"查看所有拓扑","thisNode":"查看数据","myMonitorInfo":"我的监控项","updateMyDevicePage":"修改拓扑图","updateMyMonitorInfo":"修改监控项","myDevicePage":"我的拓扑图","mySetting":"个人设置","checkAllInfos":"查看详细数据"}
                 * thisNode : 服务监控
                 */

                private ShowDataBean showData;
                private String thisNode;

                public ShowDataBean getShowData() {
                    return showData;
                }

                public void setShowData(ShowDataBean showData) {
                    this.showData = showData;
                }

                public String getThisNode() {
                    return thisNode;
                }

                public void setThisNode(String thisNode) {
                    this.thisNode = thisNode;
                }

                public static class ShowDataBean {
                    /**
                     * allDevicePage : 查看所有拓扑
                     * thisNode : 查看数据
                     * myMonitorInfo : 我的监控项
                     * updateMyDevicePage : 修改拓扑图
                     * updateMyMonitorInfo : 修改监控项
                     * myDevicePage : 我的拓扑图
                     * mySetting : 个人设置
                     * checkAllInfos : 查看详细数据
                     */

                    private String allDevicePage;
                    private String thisNode;
                    private String myMonitorInfo;
                    private String updateMyDevicePage;
                    private String updateMyMonitorInfo;
                    private String myDevicePage;
                    private String mySetting;
                    private String checkAllInfos;

                    public String getAllDevicePage() {
                        return allDevicePage;
                    }

                    public void setAllDevicePage(String allDevicePage) {
                        this.allDevicePage = allDevicePage;
                    }

                    public String getThisNode() {
                        return thisNode;
                    }

                    public void setThisNode(String thisNode) {
                        this.thisNode = thisNode;
                    }

                    public String getMyMonitorInfo() {
                        return myMonitorInfo;
                    }

                    public void setMyMonitorInfo(String myMonitorInfo) {
                        this.myMonitorInfo = myMonitorInfo;
                    }

                    public String getUpdateMyDevicePage() {
                        return updateMyDevicePage;
                    }

                    public void setUpdateMyDevicePage(String updateMyDevicePage) {
                        this.updateMyDevicePage = updateMyDevicePage;
                    }

                    public String getUpdateMyMonitorInfo() {
                        return updateMyMonitorInfo;
                    }

                    public void setUpdateMyMonitorInfo(String updateMyMonitorInfo) {
                        this.updateMyMonitorInfo = updateMyMonitorInfo;
                    }

                    public String getMyDevicePage() {
                        return myDevicePage;
                    }

                    public void setMyDevicePage(String myDevicePage) {
                        this.myDevicePage = myDevicePage;
                    }

                    public String getMySetting() {
                        return mySetting;
                    }

                    public void setMySetting(String mySetting) {
                        this.mySetting = mySetting;
                    }

                    public String getCheckAllInfos() {
                        return checkAllInfos;
                    }

                    public void setCheckAllInfos(String checkAllInfos) {
                        this.checkAllInfos = checkAllInfos;
                    }
                }
            }

            public static class AssistConsoleBean {
                /**
                 * monitoringManager : {"taskMonitoring":"任务监控","thisNode":"监控管理","executorMonitoring":"线程监控"}
                 * thisNode : 飞鸽小秘书
                 * interceptManager : {"userIntercept":"用户拦截","thisNode":"拦截管理","blacklistIntercep":"黑名单拦截"}
                 * messageManager : {"ruleInterceptMsg":"规则拦截消息","messagePushList":"消息推送列表","thisNode":"消息管理","blacklistMsg":"黑名单消息"}
                 */

                private MonitoringManagerBean monitoringManager;
                private String thisNode;
                private InterceptManagerBean interceptManager;
                private MessageManagerBean messageManager;

                public MonitoringManagerBean getMonitoringManager() {
                    return monitoringManager;
                }

                public void setMonitoringManager(MonitoringManagerBean monitoringManager) {
                    this.monitoringManager = monitoringManager;
                }

                public String getThisNode() {
                    return thisNode;
                }

                public void setThisNode(String thisNode) {
                    this.thisNode = thisNode;
                }

                public InterceptManagerBean getInterceptManager() {
                    return interceptManager;
                }

                public void setInterceptManager(InterceptManagerBean interceptManager) {
                    this.interceptManager = interceptManager;
                }

                public MessageManagerBean getMessageManager() {
                    return messageManager;
                }

                public void setMessageManager(MessageManagerBean messageManager) {
                    this.messageManager = messageManager;
                }

                public static class MonitoringManagerBean {
                    /**
                     * taskMonitoring : 任务监控
                     * thisNode : 监控管理
                     * executorMonitoring : 线程监控
                     */

                    private String taskMonitoring;
                    private String thisNode;
                    private String executorMonitoring;

                    public String getTaskMonitoring() {
                        return taskMonitoring;
                    }

                    public void setTaskMonitoring(String taskMonitoring) {
                        this.taskMonitoring = taskMonitoring;
                    }

                    public String getThisNode() {
                        return thisNode;
                    }

                    public void setThisNode(String thisNode) {
                        this.thisNode = thisNode;
                    }

                    public String getExecutorMonitoring() {
                        return executorMonitoring;
                    }

                    public void setExecutorMonitoring(String executorMonitoring) {
                        this.executorMonitoring = executorMonitoring;
                    }
                }

                public static class InterceptManagerBean {
                    /**
                     * userIntercept : 用户拦截
                     * thisNode : 拦截管理
                     * blacklistIntercep : 黑名单拦截
                     */

                    private String userIntercept;
                    private String thisNode;
                    private String blacklistIntercep;

                    public String getUserIntercept() {
                        return userIntercept;
                    }

                    public void setUserIntercept(String userIntercept) {
                        this.userIntercept = userIntercept;
                    }

                    public String getThisNode() {
                        return thisNode;
                    }

                    public void setThisNode(String thisNode) {
                        this.thisNode = thisNode;
                    }

                    public String getBlacklistIntercep() {
                        return blacklistIntercep;
                    }

                    public void setBlacklistIntercep(String blacklistIntercep) {
                        this.blacklistIntercep = blacklistIntercep;
                    }
                }

                public static class MessageManagerBean {
                    /**
                     * ruleInterceptMsg : 规则拦截消息
                     * messagePushList : 消息推送列表
                     * thisNode : 消息管理
                     * blacklistMsg : 黑名单消息
                     */

                    private String ruleInterceptMsg;
                    private String messagePushList;
                    private String thisNode;
                    private String blacklistMsg;

                    public String getRuleInterceptMsg() {
                        return ruleInterceptMsg;
                    }

                    public void setRuleInterceptMsg(String ruleInterceptMsg) {
                        this.ruleInterceptMsg = ruleInterceptMsg;
                    }

                    public String getMessagePushList() {
                        return messagePushList;
                    }

                    public void setMessagePushList(String messagePushList) {
                        this.messagePushList = messagePushList;
                    }

                    public String getThisNode() {
                        return thisNode;
                    }

                    public void setThisNode(String thisNode) {
                        this.thisNode = thisNode;
                    }

                    public String getBlacklistMsg() {
                        return blacklistMsg;
                    }

                    public void setBlacklistMsg(String blacklistMsg) {
                        this.blacklistMsg = blacklistMsg;
                    }
                }
            }
        }

        public static class FxClientBean {
            /**
             * thisNode : 飞鸽客户端
             * android : {"workcenter":{"l2Leave":"请假申请","eventTrack":"事项跟踪","questionnaire":"问卷调查","notes":"通知公告","activity":"企业活动","oaCompanyNews":"公司新闻","companyPolicy":"公司政策","myPerformance":"我的业绩","recommend":"内部推荐","hr":"人力","mySalary":"我的薪资","meterialOut":"物资出门","oaMyApplications":"我的申请","myAttendance":"我的出勤","addMore":"添加更多","oaMyReading":"待阅事宜","myChecked":"我已审核","personalCapacity":"个人产能","vehicleManagement":"车辆管理","newsSearch":"新闻搜索","conferenceAssist":"会议助手","companyNews":"公司新闻","myExpress":"我的快递","businessTrip":"出差申请","clockOut":"外出打卡","studyExam":"学习考试","bill":"开票信息","waterAndElectricity":"水电","equipments":"设备","oaHome":"OA首页","suggestionBox":"建议箱","equipmentRepair":"设备报修","materials":"物料","myChecking":"待我审核","thisNode":"工作中心","ePatrol":"电子巡更","overtime":"加班申请","illegals":"违规违纪","assetInventory":"资产盘点","teleconference":"电话会议","visitorReception":"来访接待","attendance":"考勤处理","networkIntercom":"网络对讲"},"thisNode":"安卓客户端","sip":"SIP电话","chatMessage":{"thisNode":"聊天消息","photo":"图片消息","text":"文本消息","audio":"语音消息","video":"视频消息","emoj":"表情消息"}}
             * ios : {"workcenter":{"l2Leave":"请假申请","eventTrack":"事项跟踪","questionnaire":"问卷调查","notes":"通知公告","activity":"企业活动","oaCompanyNews":"公司新闻","companyPolicy":"公司政策","myPerformance":"我的业绩","recommend":"内部推荐","hr":"人力","mySalary":"我的薪资","meterialOut":"物资出门","oaMyApplications":"我的申请","myAttendance":"我的出勤","addMore":"添加更多","oaMyReading":"待阅事宜","myChecked":"我已审核","personalCapacity":"个人产能","vehicleManagement":"车辆管理","newsSearch":"新闻搜索","conferenceAssist":"会议助手","companyNews":"公司新闻","myExpress":"我的快递","businessTrip":"出差申请","clockOut":"外出打卡","studyExam":"学习考试","bill":"开票信息","waterAndElectricity":"水电","equipments":"设备","oaHome":"OA首页","suggestionBox":"建议箱","equipmentRepair":"设备报修","materials":"物料","myChecking":"待我审核","thisNode":"工作中心","ePatrol":"电子巡更","overtime":"加班申请","illegals":"违规违纪","assetInventory":"资产盘点","teleconference":"电话会议","visitorReception":"来访接待","attendance":"考勤处理","networkIntercom":"网络对讲"},"thisNode":"苹果客户端","sip":"SIP电话","chatMessage":{"thisNode":"聊天消息","photo":"图片消息","text":"文本消息","audio":"语音消息","video":"视频消息","emoj":"表情消息"}}
             */

            private String thisNode;
            private AndroidBean android;
            private IosBean ios;

            public String getThisNode() {
                return thisNode;
            }

            public void setThisNode(String thisNode) {
                this.thisNode = thisNode;
            }

            public AndroidBean getAndroid() {
                return android;
            }

            public void setAndroid(AndroidBean android) {
                this.android = android;
            }

            public IosBean getIos() {
                return ios;
            }

            public void setIos(IosBean ios) {
                this.ios = ios;
            }

            public static class AndroidBean {
                /**
                 * workcenter : {"l2Leave":"请假申请","eventTrack":"事项跟踪","questionnaire":"问卷调查","notes":"通知公告","activity":"企业活动","oaCompanyNews":"公司新闻","companyPolicy":"公司政策","myPerformance":"我的业绩","recommend":"内部推荐","hr":"人力","mySalary":"我的薪资","meterialOut":"物资出门","oaMyApplications":"我的申请","myAttendance":"我的出勤","addMore":"添加更多","oaMyReading":"待阅事宜","myChecked":"我已审核","personalCapacity":"个人产能","vehicleManagement":"车辆管理","newsSearch":"新闻搜索","conferenceAssist":"会议助手","companyNews":"公司新闻","myExpress":"我的快递","businessTrip":"出差申请","clockOut":"外出打卡","studyExam":"学习考试","bill":"开票信息","waterAndElectricity":"水电","equipments":"设备","oaHome":"OA首页","suggestionBox":"建议箱","equipmentRepair":"设备报修","materials":"物料","myChecking":"待我审核","thisNode":"工作中心","ePatrol":"电子巡更","overtime":"加班申请","illegals":"违规违纪","assetInventory":"资产盘点","teleconference":"电话会议","visitorReception":"来访接待","attendance":"考勤处理","networkIntercom":"网络对讲"}
                 * thisNode : 安卓客户端
                 * sip : SIP电话
                 * chatMessage : {"thisNode":"聊天消息","photo":"图片消息","text":"文本消息","audio":"语音消息","video":"视频消息","emoj":"表情消息"}
                 */

                private WorkCenterBean workcenter;
                private String thisNode;
                private String sip;
                private ChatMessageBean chatMessage;

                public WorkCenterBean getWorkcenter() {
                    return workcenter;
                }

                public void setWorkcenter(WorkCenterBean workcenter) {
                    this.workcenter = workcenter;
                }

                public String getThisNode() {
                    return thisNode;
                }

                public void setThisNode(String thisNode) {
                    this.thisNode = thisNode;
                }

                public String getSip() {
                    return sip;
                }

                public void setSip(String sip) {
                    this.sip = sip;
                }

                public ChatMessageBean getChatMessage() {
                    return chatMessage;
                }

                public void setChatMessage(ChatMessageBean chatMessage) {
                    this.chatMessage = chatMessage;
                }

                public static class ChatMessageBean {
                    /**
                     * thisNode : 聊天消息
                     * photo : 图片消息
                     * text : 文本消息
                     * audio : 语音消息
                     * video : 视频消息
                     * emoj : 表情消息
                     */

                    private String thisNode;
                    private String photo;
                    private String text;
                    private String audio;
                    private String video;
                    private String emoj;

                    public String getThisNode() {
                        return thisNode;
                    }

                    public void setThisNode(String thisNode) {
                        this.thisNode = thisNode;
                    }

                    public String getPhoto() {
                        return photo;
                    }

                    public void setPhoto(String photo) {
                        this.photo = photo;
                    }

                    public String getText() {
                        return text;
                    }

                    public void setText(String text) {
                        this.text = text;
                    }

                    public String getAudio() {
                        return audio;
                    }

                    public void setAudio(String audio) {
                        this.audio = audio;
                    }

                    public String getVideo() {
                        return video;
                    }

                    public void setVideo(String video) {
                        this.video = video;
                    }

                    public String getEmoj() {
                        return emoj;
                    }

                    public void setEmoj(String emoj) {
                        this.emoj = emoj;
                    }
                }
            }

            public static class IosBean {
                /**
                 * workcenter : {"l2Leave":"请假申请","eventTrack":"事项跟踪","questionnaire":"问卷调查","notes":"通知公告","activity":"企业活动","oaCompanyNews":"公司新闻","companyPolicy":"公司政策","myPerformance":"我的业绩","recommend":"内部推荐","hr":"人力","mySalary":"我的薪资","meterialOut":"物资出门","oaMyApplications":"我的申请","myAttendance":"我的出勤","addMore":"添加更多","oaMyReading":"待阅事宜","myChecked":"我已审核","personalCapacity":"个人产能","vehicleManagement":"车辆管理","newsSearch":"新闻搜索","conferenceAssist":"会议助手","companyNews":"公司新闻","myExpress":"我的快递","businessTrip":"出差申请","clockOut":"外出打卡","studyExam":"学习考试","bill":"开票信息","waterAndElectricity":"水电","equipments":"设备","oaHome":"OA首页","suggestionBox":"建议箱","equipmentRepair":"设备报修","materials":"物料","myChecking":"待我审核","thisNode":"工作中心","ePatrol":"电子巡更","overtime":"加班申请","illegals":"违规违纪","assetInventory":"资产盘点","teleconference":"电话会议","visitorReception":"来访接待","attendance":"考勤处理","networkIntercom":"网络对讲"}
                 * thisNode : 苹果客户端
                 * sip : SIP电话
                 * chatMessage : {"thisNode":"聊天消息","photo":"图片消息","text":"文本消息","audio":"语音消息","video":"视频消息","emoj":"表情消息"}
                 */

                private WorkcenterBeanX workcenter;
                private String thisNode;
                private String sip;
                private ChatMessageBeanX chatMessage;

                public WorkcenterBeanX getWorkcenter() {
                    return workcenter;
                }

                public void setWorkcenter(WorkcenterBeanX workcenter) {
                    this.workcenter = workcenter;
                }

                public String getThisNode() {
                    return thisNode;
                }

                public void setThisNode(String thisNode) {
                    this.thisNode = thisNode;
                }

                public String getSip() {
                    return sip;
                }

                public void setSip(String sip) {
                    this.sip = sip;
                }

                public ChatMessageBeanX getChatMessage() {
                    return chatMessage;
                }

                public void setChatMessage(ChatMessageBeanX chatMessage) {
                    this.chatMessage = chatMessage;
                }

                public static class WorkcenterBeanX {
                    /**
                     * l2Leave : 请假申请
                     * eventTrack : 事项跟踪
                     * questionnaire : 问卷调查
                     * notes : 通知公告
                     * activity : 企业活动
                     * oaCompanyNews : 公司新闻
                     * companyPolicy : 公司政策
                     * myPerformance : 我的业绩
                     * recommend : 内部推荐
                     * hr : 人力
                     * mySalary : 我的薪资
                     * meterialOut : 物资出门
                     * oaMyApplications : 我的申请
                     * myAttendance : 我的出勤
                     * addMore : 添加更多
                     * oaMyReading : 待阅事宜
                     * myChecked : 我已审核
                     * personalCapacity : 个人产能
                     * vehicleManagement : 车辆管理
                     * newsSearch : 新闻搜索
                     * conferenceAssist : 会议助手
                     * companyNews : 公司新闻
                     * myExpress : 我的快递
                     * businessTrip : 出差申请
                     * clockOut : 外出打卡
                     * studyExam : 学习考试
                     * bill : 开票信息
                     * waterAndElectricity : 水电
                     * equipments : 设备
                     * oaHome : OA首页
                     * suggestionBox : 建议箱
                     * equipmentRepair : 设备报修
                     * materials : 物料
                     * myChecking : 待我审核
                     * thisNode : 工作中心
                     * ePatrol : 电子巡更
                     * overtime : 加班申请
                     * illegals : 违规违纪
                     * assetInventory : 资产盘点
                     * teleconference : 电话会议
                     * visitorReception : 来访接待
                     * attendance : 考勤处理
                     * networkIntercom : 网络对讲
                     */

                    private String l2Leave;
                    private String eventTrack;
                    private String questionnaire;
                    private String notes;
                    private String activity;
                    private String oaCompanyNews;
                    private String companyPolicy;
                    private String myPerformance;
                    private String recommend;
                    private String hr;
                    private String mySalary;
                    private String meterialOut;
                    private String oaMyApplications;
                    private String myAttendance;
                    private String addMore;
                    private String oaMyReading;
                    private String myChecked;
                    private String personalCapacity;
                    private String vehicleManagement;
                    private String newsSearch;
                    private String conferenceAssist;
                    private String companyNews;
                    private String myExpress;
                    private String businessTrip;
                    private String clockOut;
                    private String studyExam;
                    private String bill;
                    private String waterAndElectricity;
                    private String equipments;
                    private String oaHome;
                    private String suggestionBox;
                    private String equipmentRepair;
                    private String materials;
                    private String myChecking;
                    private String thisNode;
                    private String ePatrol;
                    private String overtime;
                    private String illegals;
                    private String assetInventory;
                    private String teleconference;
                    private String visitorReception;
                    private String attendance;
                    private String networkIntercom;

                    public String getL2Leave() {
                        return l2Leave;
                    }

                    public void setL2Leave(String l2Leave) {
                        this.l2Leave = l2Leave;
                    }

                    public String getEventTrack() {
                        return eventTrack;
                    }

                    public void setEventTrack(String eventTrack) {
                        this.eventTrack = eventTrack;
                    }

                    public String getQuestionnaire() {
                        return questionnaire;
                    }

                    public void setQuestionnaire(String questionnaire) {
                        this.questionnaire = questionnaire;
                    }

                    public String getNotes() {
                        return notes;
                    }

                    public void setNotes(String notes) {
                        this.notes = notes;
                    }

                    public String getActivity() {
                        return activity;
                    }

                    public void setActivity(String activity) {
                        this.activity = activity;
                    }

                    public String getOaCompanyNews() {
                        return oaCompanyNews;
                    }

                    public void setOaCompanyNews(String oaCompanyNews) {
                        this.oaCompanyNews = oaCompanyNews;
                    }

                    public String getCompanyPolicy() {
                        return companyPolicy;
                    }

                    public void setCompanyPolicy(String companyPolicy) {
                        this.companyPolicy = companyPolicy;
                    }

                    public String getMyPerformance() {
                        return myPerformance;
                    }

                    public void setMyPerformance(String myPerformance) {
                        this.myPerformance = myPerformance;
                    }

                    public String getRecommend() {
                        return recommend;
                    }

                    public void setRecommend(String recommend) {
                        this.recommend = recommend;
                    }

                    public String getHr() {
                        return hr;
                    }

                    public void setHr(String hr) {
                        this.hr = hr;
                    }

                    public String getMySalary() {
                        return mySalary;
                    }

                    public void setMySalary(String mySalary) {
                        this.mySalary = mySalary;
                    }

                    public String getMeterialOut() {
                        return meterialOut;
                    }

                    public void setMeterialOut(String meterialOut) {
                        this.meterialOut = meterialOut;
                    }

                    public String getOaMyApplications() {
                        return oaMyApplications;
                    }

                    public void setOaMyApplications(String oaMyApplications) {
                        this.oaMyApplications = oaMyApplications;
                    }

                    public String getMyAttendance() {
                        return myAttendance;
                    }

                    public void setMyAttendance(String myAttendance) {
                        this.myAttendance = myAttendance;
                    }

                    public String getAddMore() {
                        return addMore;
                    }

                    public void setAddMore(String addMore) {
                        this.addMore = addMore;
                    }

                    public String getOaMyReading() {
                        return oaMyReading;
                    }

                    public void setOaMyReading(String oaMyReading) {
                        this.oaMyReading = oaMyReading;
                    }

                    public String getMyChecked() {
                        return myChecked;
                    }

                    public void setMyChecked(String myChecked) {
                        this.myChecked = myChecked;
                    }

                    public String getPersonalCapacity() {
                        return personalCapacity;
                    }

                    public void setPersonalCapacity(String personalCapacity) {
                        this.personalCapacity = personalCapacity;
                    }

                    public String getVehicleManagement() {
                        return vehicleManagement;
                    }

                    public void setVehicleManagement(String vehicleManagement) {
                        this.vehicleManagement = vehicleManagement;
                    }

                    public String getNewsSearch() {
                        return newsSearch;
                    }

                    public void setNewsSearch(String newsSearch) {
                        this.newsSearch = newsSearch;
                    }

                    public String getConferenceAssist() {
                        return conferenceAssist;
                    }

                    public void setConferenceAssist(String conferenceAssist) {
                        this.conferenceAssist = conferenceAssist;
                    }

                    public String getCompanyNews() {
                        return companyNews;
                    }

                    public void setCompanyNews(String companyNews) {
                        this.companyNews = companyNews;
                    }

                    public String getMyExpress() {
                        return myExpress;
                    }

                    public void setMyExpress(String myExpress) {
                        this.myExpress = myExpress;
                    }

                    public String getBusinessTrip() {
                        return businessTrip;
                    }

                    public void setBusinessTrip(String businessTrip) {
                        this.businessTrip = businessTrip;
                    }

                    public String getClockOut() {
                        return clockOut;
                    }

                    public void setClockOut(String clockOut) {
                        this.clockOut = clockOut;
                    }

                    public String getStudyExam() {
                        return studyExam;
                    }

                    public void setStudyExam(String studyExam) {
                        this.studyExam = studyExam;
                    }

                    public String getBill() {
                        return bill;
                    }

                    public void setBill(String bill) {
                        this.bill = bill;
                    }

                    public String getWaterAndElectricity() {
                        return waterAndElectricity;
                    }

                    public void setWaterAndElectricity(String waterAndElectricity) {
                        this.waterAndElectricity = waterAndElectricity;
                    }

                    public String getEquipments() {
                        return equipments;
                    }

                    public void setEquipments(String equipments) {
                        this.equipments = equipments;
                    }

                    public String getOaHome() {
                        return oaHome;
                    }

                    public void setOaHome(String oaHome) {
                        this.oaHome = oaHome;
                    }

                    public String getSuggestionBox() {
                        return suggestionBox;
                    }

                    public void setSuggestionBox(String suggestionBox) {
                        this.suggestionBox = suggestionBox;
                    }

                    public String getEquipmentRepair() {
                        return equipmentRepair;
                    }

                    public void setEquipmentRepair(String equipmentRepair) {
                        this.equipmentRepair = equipmentRepair;
                    }

                    public String getMaterials() {
                        return materials;
                    }

                    public void setMaterials(String materials) {
                        this.materials = materials;
                    }

                    public String getMyChecking() {
                        return myChecking;
                    }

                    public void setMyChecking(String myChecking) {
                        this.myChecking = myChecking;
                    }

                    public String getThisNode() {
                        return thisNode;
                    }

                    public void setThisNode(String thisNode) {
                        this.thisNode = thisNode;
                    }

                    public String getEPatrol() {
                        return ePatrol;
                    }

                    public void setEPatrol(String ePatrol) {
                        this.ePatrol = ePatrol;
                    }

                    public String getOvertime() {
                        return overtime;
                    }

                    public void setOvertime(String overtime) {
                        this.overtime = overtime;
                    }

                    public String getIllegals() {
                        return illegals;
                    }

                    public void setIllegals(String illegals) {
                        this.illegals = illegals;
                    }

                    public String getAssetInventory() {
                        return assetInventory;
                    }

                    public void setAssetInventory(String assetInventory) {
                        this.assetInventory = assetInventory;
                    }

                    public String getTeleconference() {
                        return teleconference;
                    }

                    public void setTeleconference(String teleconference) {
                        this.teleconference = teleconference;
                    }

                    public String getVisitorReception() {
                        return visitorReception;
                    }

                    public void setVisitorReception(String visitorReception) {
                        this.visitorReception = visitorReception;
                    }

                    public String getAttendance() {
                        return attendance;
                    }

                    public void setAttendance(String attendance) {
                        this.attendance = attendance;
                    }

                    public String getNetworkIntercom() {
                        return networkIntercom;
                    }

                    public void setNetworkIntercom(String networkIntercom) {
                        this.networkIntercom = networkIntercom;
                    }
                }

                public static class ChatMessageBeanX {
                    /**
                     * thisNode : 聊天消息
                     * photo : 图片消息
                     * text : 文本消息
                     * audio : 语音消息
                     * video : 视频消息
                     * emoj : 表情消息
                     */

                    private String thisNode;
                    private String photo;
                    private String text;
                    private String audio;
                    private String video;
                    private String emoj;

                    public String getThisNode() {
                        return thisNode;
                    }

                    public void setThisNode(String thisNode) {
                        this.thisNode = thisNode;
                    }

                    public String getPhoto() {
                        return photo;
                    }

                    public void setPhoto(String photo) {
                        this.photo = photo;
                    }

                    public String getText() {
                        return text;
                    }

                    public void setText(String text) {
                        this.text = text;
                    }

                    public String getAudio() {
                        return audio;
                    }

                    public void setAudio(String audio) {
                        this.audio = audio;
                    }

                    public String getVideo() {
                        return video;
                    }

                    public void setVideo(String video) {
                        this.video = video;
                    }

                    public String getEmoj() {
                        return emoj;
                    }

                    public void setEmoj(String emoj) {
                        this.emoj = emoj;
                    }
                }
            }
        }

        public static class FxServerBean {
            /**
             * thisNode : 飞鸽通信
             * foo : foo
             */

            private String thisNode;
            private String foo;

            public String getThisNode() {
                return thisNode;
            }

            public void setThisNode(String thisNode) {
                this.thisNode = thisNode;
            }

            public String getFoo() {
                return foo;
            }

            public void setFoo(String foo) {
                this.foo = foo;
            }
        }
    }
}
