package com.brins.commom.toast;

/**
 * 规则一、添加配置必须满足以下格式(包括注释)：
 * public static final int VARIABLE_NAME = SPECIAL_BASE + ${NUMBER};//类型含义//展示该类型菊花的路径
 * 规则二、后添加的id必须比之前添加的id大
 * 如果不满足上述规则，将不能通过编译
 * Created by xiaofeiyu on 2018/3/28.
 */

public class LoadingTypes {
    // 通用类型在 COMMON_BASE 与 SPECIAL_BASE 之间
    private static final int COMMON_BASE = 0; //base//通用

    public final static int DEFAULT = COMMON_BASE + 1; //其它//通用

    public final static int LIST_HEADER = COMMON_BASE + 2; //列表头//通用

    public final static int LIST_FOOTER = COMMON_BASE + 3; //列表底//通用

    public final static int FLOATING = COMMON_BASE + 4; //浮窗//通用

    public final static int START = COMMON_BASE + 5; //开始//通用

    public static final int CHANGE_STEAM = COMMON_BASE + 6;//切流//视频区

    public static final int WHITE_LOADING = COMMON_BASE + 7;//通用白色菊花//可以参考9108版本MV播放页面，位置不固定

    public static final int KTV_MV_LOADING = COMMON_BASE + 8;//KTV白色菊花//参考9108版本ktv作品播放页加载菊花

    public static final int MINIAPP_LOADING = COMMON_BASE + 9;//UI框架//小程序Loading

    public static final int MV_FEE_LOADING = COMMON_BASE + 10;//mv鉴权白色菊花//通用

    public static final int MV_BUF_LOADING = COMMON_BASE + 11;//mv缓冲白色菊花//通用

    public static final int SONG_PLAY = COMMON_BASE + 12;//歌曲播放//通用



    public static final int FEE_CHECK = COMMON_BASE + 41; //鉴权浮窗//通用

    public static final int COMMENT = COMMON_BASE + 42; //打开评论浮窗//通用

    public static final int SHARE = COMMON_BASE + 43; //分享浮窗//通用

    public static final int KUBI_RECHARGE = COMMON_BASE + 44; //酷币充值//通用

    public static final int ALBUM_BUY = COMMON_BASE + 45; //专辑购买//非音乐包通用

    public static final int MV_FEE_CHECK = COMMON_BASE + 46; //Mv鉴权//通用

    public static final int CHINAMOBILE_VIP_EXCHANGE = COMMON_BASE + 47; //积分兑换//兑换

    public static final int SCENE_RECOMMEND_EDIT = COMMON_BASE + 48; //个人空间//我的推荐

    public static final int VIP_DISCOUNT_RECEIVE = COMMON_BASE + 49; //新老用户挽回挽回//领取

    //自定义类型必须要在 SPECIAL_BASE 以上
    private static final int SPECIAL_BASE = 100;//base//通用

    public static final int SEARCH_RESULT_SONG = SPECIAL_BASE + 1; //搜索结果-歌曲//点击搜索-输入搜索内容-点击搜索

    public static final int SEARCH_RESULT_MV = SPECIAL_BASE + 2; //搜索结果-MV//点击搜索-输入搜索内容-点击搜索-点击MVtab

    public static final int SEARCH_RESULT_ALBUM = SPECIAL_BASE + 3; //搜索结果-专辑//点击搜索-输入搜索内容-点击搜索-点击专辑tab

    public static final int SEARCH_RESULT_SONGLIST = SPECIAL_BASE + 4; //搜索结果-歌单//点击搜索-输入搜索内容-点击搜索-点击歌单tab

    public static final int SEARCH_RESULT_USER = SPECIAL_BASE + 5; //搜索结果-用户//点击搜索-点击输入内容-点击搜索-点击用户tab

    public static final int SEARCH_RESULT_LYRIC = SPECIAL_BASE + 6; //搜索结果-歌词//点击搜索-点击输入内容-点击搜索-点击歌词tab

    public static final int PLAYER_FRAGMENT_RIGHT_INFO = SPECIAL_BASE + 7; //播放页-右侧歌曲信息//点击播放bar-在播放页右左划看到歌曲信息页

    public static final int SEARCH_RESULT_PROGRAM = SPECIAL_BASE + 8; //搜索结果-有声电台//点击搜索-输入搜索内容-点击搜索-点击有声电台tab

    public static final int SEARCH_RESULT_ALL = SPECIAL_BASE + 9; //搜索结果-综合//点击搜索-输入搜索内容-点击搜索-综合tab

    public static final int SEARCH_RESULT_SINGER = SPECIAL_BASE + 10; //搜索结果-歌手//点击搜索-输入搜索内容-点击搜索-点击歌手tab

    public static final int SEARCH_RESULT_KSONG = SPECIAL_BASE + 11; //搜索结果-K歌//点击搜索-输入搜索内容-点击搜索-点击K歌tab

    public static final int MINE_TAG_FOLLOW_SINGER = SPECIAL_BASE + 12; //首页-我的tab//点击主播群右侧小人头

    public static final int MINE_TAG_FOLLOW_SINGER_FOOTER = SPECIAL_BASE + 13; //首页-我的tab//点击主播群右侧小人头(翻页)

    public static final int UPDATE_BG = SPECIAL_BASE + 14; //更换背景图片//个人中心-点击背景
    public static final int LOAD_DATA = SPECIAL_BASE + 15; //加载所有背景图片//个人中心-点击背景
    public static final int KTV_MID_CHANGCHANG = SPECIAL_BASE + 16; //播放页//点击麦克风跳转中间页
    public static final int PLAYER_FRAGMENT_DOWN_PAGE_INFO = SPECIAL_BASE + 17; //播放页上拉相似歌曲页//点击播放bar-在播放页上拉相似歌曲页

    public static final int CHILD_MODE_SWICH_DIALOG = SPECIAL_BASE + 18; //切换亲子模式弹窗//侧边栏-点击亲子模式或搜索结果亲子模式h5跳转

    public static final int LBOOK_H5_VIP_CENTER = SPECIAL_BASE + 19; //会员中心-听书会员TAB//我的-点击已登录会员VIP图标-切换到听书会员tab
    public static final int LBOOK_NAV_PULL_DOWN_REFRESH = SPECIAL_BASE + 20; //首页-发现//有声电台-精选-下拉刷新
    public static final int SV_CC_VIDEO_DIALOG = SPECIAL_BASE + 21; //短视频串串视频加载菊花//打开串串开关，或者视频切换加载菊花

    public static final int LBOOK_BUY_PAYMENT_KUBI = SPECIAL_BASE + 22; //长音频购买-酷币支付//长音频购买弹窗-立即购买-选择酷币支付
    public static final int LBOOK_BUY_PAYMENT_TICKET = SPECIAL_BASE + 23; //长音频购买-听书券抵扣//长音频购买弹窗-立即购买-选择听书券抵扣
    public static final int DISCOVERY_MV_LIST = SPECIAL_BASE + 24; //首页视频列表//首页-视频-视频列表
    public static final int DISCOVERY_MV_DETAIL = SPECIAL_BASE + 25; //MV播放页//从视频列表点击进入视频播放页
    
    public static final int KTV_LBS_LOCATION_FAIL = SPECIAL_BASE + 26; //单曲排行榜-地区//点击K歌-点歌-点击某一项-点击地区tab
    public static final int KTV_PLAY_OPUS_FAIL = SPECIAL_BASE + 27; //作品播放页//点击K歌-推荐-点击关注tab-点击一项作品进入

    public static final int PLAYER_FRAGMENT_RESUME = SPECIAL_BASE + 28; //播放页//播放页-可见时上报该红叉为了记录进入播放页的uv

    public static final int PERSONFM_LOADING = SPECIAL_BASE + 29; //猜你喜欢loading//猜你喜欢自定义的loading
    public static final int PERSONFM_SPECIAL = SPECIAL_BASE + 30; //猜你喜欢特殊使用//QA的同事要求猜你喜欢进入要报一次红叉用于统计页面UV

    public static final int TING_SV_VIDEO_INIT = SPECIAL_BASE + 31; //首页//短视频tab-初始化
    public static final int TING_SV_VIDEO_AUTO_REFRESH = SPECIAL_BASE + 32; //首页//短视频tab-定时刷新
    public static final int TING_SV_VIDEO_SCROLL_REFRESH = SPECIAL_BASE + 33; //首页//短视频tab-滚动到底部刷新
    public static final int TING_SV_VIDEO_RETRY = SPECIAL_BASE + 34; //首页//短视频tab-重试刷新
    public static final int TING_SV_VIDEO_CLICK_REFRESH = SPECIAL_BASE + 35; //首页//短视频tab-点击tab刷新
    public static final int TING_SV_VIDEO_PULL_REFRESH = SPECIAL_BASE + 36; //首页//短视频tab-下拉刷新

    public static final int SEARCH_RESULT_NOVEL = SPECIAL_BASE + 37; //搜索结果-K歌//点击搜索-输入搜索内容-点击搜索-点击看小说tab

    public static final int SEARCH_RESULT_RINGTONE = SPECIAL_BASE + 38; //搜索结果-K歌//点击搜索-输入搜索内容-点击搜索-点击铃声tab
    public static final int SEND_LOCAL_ACC_FILE = SPECIAL_BASE + 39; //我的-本地//下载-伴奏tab-更多-发送文件

    public static final int PLAYER_FRAGMENT_DOWN_SONG_MULTI = SPECIAL_BASE + 40; //播放页上拉更多歌手版本//点击播放bar-在播放页上拉更多歌手版本
    public static final int PLAYER_FRAGMENT_DOWN_SONG_SIMILAR = SPECIAL_BASE + 41; //播放页上拉相似版本//点击播放bar-在播放页上拉更多歌手版本

    public static final int SEARCH_RESULT_USER_NEW = SPECIAL_BASE + 42; //搜索结果-用户//点击搜索-点击输入内容-点击搜索-点击用户tab

}