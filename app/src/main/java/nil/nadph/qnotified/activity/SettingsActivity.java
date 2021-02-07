/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.activity;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import androidx.core.text.*;
import androidx.core.view.*;

import com.rymmmmm.hook.*;
import com.tencent.mobileqq.widget.*;

import java.io.*;

import me.ketal.activity.*;
import me.ketal.hook.*;
import me.kyuubiran.hook.*;
import me.nextalone.hook.*;
import me.singleneuron.activity.*;
import me.singleneuron.hook.*;
import me.singleneuron.hook.decorator.*;
import me.singleneuron.qn_kernel.data.*;
import me.singleneuron.util.*;
import nil.nadph.qnotified.*;
import nil.nadph.qnotified.config.*;
import nil.nadph.qnotified.dialog.*;
import nil.nadph.qnotified.hook.*;
import nil.nadph.qnotified.ui.*;
import nil.nadph.qnotified.util.*;

import static android.view.ViewGroup.LayoutParams.*;
import static androidx.core.text.HtmlCompat.*;
import static me.singleneuron.util.KotlinUtilsKt.*;
import static me.singleneuron.util.QQVersion.*;
import static nil.nadph.qnotified.ui.ViewBuilder.*;
import static nil.nadph.qnotified.util.Utils.*;

@SuppressLint("Registered")
public class SettingsActivity extends IphoneTitleBarActivityCompat implements Runnable {

    private static final int R_ID_BTN_FILE_RECV = 0x300AFF91;
    private static final String qn_enable_fancy_rgb = "qn_enable_fancy_rgb";

    private TextView __tv_muted_atall, __tv_muted_redpacket, __tv_fake_bat_status, __recv_status, __recv_desc, __jmp_ctl_cnt;

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        String _hostName = HostInformationProviderKt.getHostInformationProvider().getHostName();
        LinearLayout ll = new LinearLayout(this);
        ll.setId(R.id.rootMainLayout);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(this);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        bounceScrollView.setId(R.id.rootBounceScrollView);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT, dip2px(this, 48));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int mar = (int) (dip2px(this, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
        __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);
        ColorStateList hiColor = ColorStateList.valueOf(Color.argb(255, 242, 140, 72));
        RelativeLayout _t;
        try {
            LinearLayout nnn = subtitle(this, "");
            TextView t = (TextView) nnn.getChildAt(0);
            NewsHelper.getCachedNews(t);
            ll.addView(nnn);
            NewsHelper.asyncFetchNewsIfNeeded(t);
        } catch (Throwable e) {
            log(e);
        }
        if (LicenseStatus.isAsserted()) {
            ll.addView(newListItemButton(this, "狐狸狸测试功能", "不管你是什么人都别动这里的东西", null, clickToProxyActAction(AlphaTestFuncActivity.class)));
        }
        if (!LicenseStatus.hasBlackFlags()) {
            ll.addView(newListItemButton(this, "Beta测试性功能", "仅用于测试稳定性", null, clickToProxyActAction(BetaTestFuncActivity.class)));
            ll.addView(newListItemButton(this, "Omega测试性功能", "这是个不存在的功能", null, v -> KotlinUtilsKt.showEulaDialog(SettingsActivity.this)));
        }
        ll.addView(subtitle(this, "基本功能"));
        if (!HostInformationProviderKt.getHostInformationProvider().isTim() && HostInformationProviderKt.getHostInformationProvider().getVersionCode() >= QQ_8_2_6) {
            ll.addView(_t = newListItemButton(this, "自定义电量", "[QQ>=8.2.6]在线模式为我的电量时生效", "N/A", clickToProxyActAction(FakeBatCfgActivity.class)));
            __tv_fake_bat_status = _t.findViewById(R_ID_VALUE);
        }
        ViewGroup _tmp_vg = newListItemButton(this, "花Q", "若无另行说明, 所有功能开关都即时生效", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RikkaDialog.showRikkaFuncDialog(SettingsActivity.this);
            }
        });
        _tmp_vg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                rgbEnabled = !rgbEnabled;
                ConfigManager cfg = ConfigManager.getDefaultConfig();
                cfg.putBoolean(qn_enable_fancy_rgb, rgbEnabled);
                try {
                    cfg.save();
                } catch (IOException ignored) {
                }
                if (rgbEnabled) {
                    startRgbIfEnabled();
                }
                return true;
            }
        });
        mRikkaTitle = _tmp_vg.findViewById(R_ID_TITLE);
        mRikkaDesc = _tmp_vg.findViewById(R_ID_DESCRIPTION);
        ll.addView(_tmp_vg);
        ll.addView(newListItemButton(this, "QQ净化[WIP]", "开发中...", null, clickToProxyActAction(me.zpp0196.qqpurify.activity.MainActivity.class)));
        ll.addView(newListItemHookSwitchInit(this, "语音转发", "长按语音消息", PttForwardHook.get()));
        ll.addView(newListItemHookSwitchInit(this, " +1", "不是复读机", RepeaterHook.get()));
        ll.addView(newListItemButton(this, "自定义+1图标", null, null, RepeaterIconSettingDialog.OnClickListener_createDialog(this)));
        ll.addView(subtitle(this, "净化设置"));
        if (ReplyNoAtHook.get().isValid()) {
            ll.addView(newListItemHookSwitchInit(this, "禁止回复自动@", "去除回复消息时自动@特性", ReplyNoAtHook.get()));
        }
        ll.addView(newListItemHookSwitchInit(this, "禁用$打开送礼界面", "禁止聊天时输入$自动弹出[选择赠送对象]窗口", $endGiftHook.get()));
        ll.addView(subtitle(this, "消息通知设置(不影响接收消息)屏蔽后可能仍有[橙字],但通知栏不会有通知,赞说说不提醒仅屏蔽通知栏的通知"));
        ll.addView(subtitle(this, "    注:屏蔽后可能仍有[橙字],但不会有通知"));
        ll.addView(_t = newListItemButton(this, "屏蔽指定群@全体成员通知", HtmlCompat.fromHtml("<font color='"
                + get_RGB(hiColor.getDefaultColor()) + "'>[@全体成员]</font>就这点破事", FROM_HTML_MODE_LEGACY), "%d个群",
            v -> TroopSelectActivity.startToSelectTroopsAndSaveToExfMgr(SettingsActivity.this, ConfigItems.qn_muted_at_all, "屏蔽@全体成员")));
        __tv_muted_atall = _t.findViewById(R_ID_VALUE);
        ll.addView(_t = newListItemButton(this, "屏蔽指定群的红包通知", HtmlCompat.fromHtml("<font color='"
                + get_RGB(hiColor.getDefaultColor()) + "'>[QQ红包][有红包]</font>恭喜发财", FROM_HTML_MODE_LEGACY), "%d个群",
            v -> TroopSelectActivity.startToSelectTroopsAndSaveToExfMgr(SettingsActivity.this, ConfigItems.qn_muted_red_packet, "屏蔽群红包")));
        __tv_muted_redpacket = _t.findViewById(R_ID_VALUE);
        ll.addView(newListItemHookSwitchInit(this, "被赞说说不提醒", "不影响评论,转发或击掌的通知", MuteQZoneThumbsUp.get()));
        ll.addView(newListItemHookSwitchInit(this, "转发消息点头像查看详细信息", "仅限合并转发的消息", MultiForwardAvatarHook.get()));
        if (!HostInformationProviderKt.getHostInformationProvider().isTim()) {
            ll.addView(subtitle(this, "图片相关"));
            ll.addView(newListItemHookSwitchInit(this, "禁止秀图自动展示", null, ShowPicGagHook.get()));
            ll.addView(newListItemHookSwitchInit(this, "禁用夜间模式遮罩", "移除夜间模式下聊天界面的深色遮罩", DarkOverlayHook.get()));
        }
        ll.addView(newListItemButton(this, "辅助功能", null, null, clickToProxyActAction(AuxFuncActivity.class)));
        ll.addView(newListItemHookSwitchInit(this, "防撤回", "自带撤回灰字提示", RevokeMsgHook.get()));
        ll.addView(newListItemHookSwitchInit(this, "显示设置禁言的管理", "即使你只是普通群成员", GagInfoDisclosure.get()));
        addViewConditionally(ll, this, "小程序分享转链接", "感谢Alcatraz323开发远离小程序,感谢神经元移植到Xposed", NoApplet.INSTANCE);
        ll.addView(subtitle(this, "实验性功能(未必有效)"));
        ll.addView(_t = newListItemButton(this, "下载重定向", "N/A", "N/A", this::onFileRecvRedirectClick));
        _t.setId(R_ID_BTN_FILE_RECV);
        __recv_desc = _t.findViewById(R_ID_DESCRIPTION);
        __recv_status = _t.findViewById(R_ID_VALUE);
        ll.addView(newListItemButton(this, "添加账号", "需要手动登录, 核心代码由 JamGmilk 提供", null, this::onAddAccountClick));
        ll.addView(newListItemHookSwitchInit(this, "屏蔽小程序广告", "需要手动关闭广告, 请勿反馈此功能无效", RemoveMiniProgramAd.get()));
        ll.addView(newListItemHookSwitchInit(this, "昵称/群名字打码", "娱乐功能 不进行维护", AutoMosaicName.INSTANCE));
        ll.addView(newListItemHookSwitchInit(this, "自己的消息和头像居左显示", "娱乐功能 不进行维护", ShowSelfMsgByLeft.INSTANCE));
        if (HostInformationProviderKt.getHostInformationProvider().getVersionCode() < QQ_8_2_0) {
            ll.addView(newListItemHookSwitchInit(this, "收藏更多表情", "[暂不支持>=8.2.0]保存在本地", FavMoreEmo.get()));
        }
        ll.addView(newListItemHookSwitchInit(this, "屏蔽更新提醒", null, PreUpgradeHook.get()));
        ll.addView(newListItemHookSwitchInit(this, "检查消息", "暂时有点用（聊天界面长按+号后点击头像）", InspectMessage.get()));
        if (!HostInformationProviderKt.getHostInformationProvider().isTim()) {
            ll.addView(newListItemHookSwitchInit(this, "自定义猜拳骰子", null, CheatHook.get()));
            ll.addView(newListItemHookSwitchInit(this, "简洁模式圆头像", "From Rikka", RoundAvatarHook.get()));
        }
        ll.addView(newListItemHookSwitchInit(this, "新版简洁模式圆头像", "From Rikka, 支持8.3.6及更高，重启后生效", NewRoundHead.INSTANCE));
        ll.addView(newListItemHookSwitchInit(this, "强制使用系统相机", "仅能录像，支持8.3.6及更高", ForceSystemCamera.INSTANCE));
        addViewConditionally(ll, this, "强制使用系统相册", "支持8.3.6及更高", ForceSystemAlbum.INSTANCE);
        ll.addView(newListItemHookSwitchInit(this, "强制使用系统文件", "支持8.3.6及更高", ForceSystemFile.INSTANCE));
        ll.addView(newListItemButton(this, "修改侧滑边距", "感谢祈无，支持8.4.1及更高，重启后生效", "", clickToProxyActAction(ChangeDrawerWidthActivity.class)));
        ll.addView(newListItemHookSwitchInit(this, "屏蔽QQ空间滑动相机", null, DisableQzoneSlideCamera.INSTANCE));
        ll.addView(newListItemHookSwitchInit(this, "回执消息文本化", null, SimpleReceiptMessage.INSTANCE));
        ll.addView(newListItemHookSwitchInit(this, "精简气泡长按菜单", null, SimplifyChatLongItem.INSTANCE));
        ll.addView(newListItemHookSwitchInit(this, "批量撤回消息", "多选消息后撤回", MultiActionHook.INSTANCE));
        if (LeftSwipeReplyHook.INSTANCE.isValid()) {
            ll.addView(newListItemButton(this, "修改消息左滑动作", "", null, clickToProxyActAction(ModifyLeftSwipeReplyActivity.class)));
        }
        if (SortAtPanel.INSTANCE.isValid()) {
            ll.addView(newListItemHookSwitchInit(this, "修改@界面排序", "排序由群主管理员至正常人员", SortAtPanel.INSTANCE));
        }

        ll.addView(subtitle(this, "好友列表"));
        ll.addView(newListItemButton(this, "打开资料卡", "打开指定用户的资料卡", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = CustomDialog.createFailsafe(SettingsActivity.this);
                Context ctx = dialog.getContext();
                EditText editText = new EditText(ctx);
                editText.setTextSize(16);
                int _5 = dip2px(SettingsActivity.this, 5);
                editText.setPadding(_5, _5, _5, _5);
                ViewCompat.setBackground(editText, new HighContrastBorder());
                LinearLayout linearLayout = new LinearLayout(ctx);
                linearLayout.addView(editText, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, _5 * 2));
                AlertDialog alertDialog = (AlertDialog) dialog.setTitle("输入对方QQ号")
                    .setView(linearLayout)
                    .setCancelable(true)
                    .setPositiveButton("确认", null)
                    .setNegativeButton("取消", null)
                    .create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = editText.getText().toString();
                        if (text.equals("")) {
                            showToast(SettingsActivity.this, TOAST_TYPE_ERROR, "请输入QQ号", Toast.LENGTH_SHORT);
                            return;
                        }
                        long uin = 0;
                        try {
                            uin = Long.parseLong(text);
                        } catch (NumberFormatException ignored) {
                        }
                        if (uin < 10000) {
                            showToast(SettingsActivity.this, TOAST_TYPE_ERROR, "请输入有效的QQ号", Toast.LENGTH_SHORT);
                            return;
                        }
                        alertDialog.dismiss();
                        MainHook.openProfileCard(SettingsActivity.this, uin);
                    }
                });
            }
        }));
        ll.addView(newListItemButton(this, "历史好友", null, null, clickToProxyActAction(ExfriendListActivity.class)));
        ll.addView(newListItemButton(this, "导出历史好友列表", "支持csv/json格式", null, clickToProxyActAction(FriendlistExportActivity.class)));
        ll.addView(newListItemConfigSwitchIfValid(this, "被删好友通知", "检测到你被好友删除后发出通知", ConfigItems.qn_notify_when_del));
        if (!HostInformationProviderKt.getHostInformationProvider().isTim()) {
            ll.addView(newListItemSwitchConfigNext(this, "隐藏分组下方入口", "隐藏分组列表最下方的历史好友按钮", ConfigItems.qn_hide_ex_entry_group, false));
        }
        ll.addView(newListItemSwitchConfigNext(this, "禁用" + _hostName + "热补丁", "一般无需开启", ConfigItems.qn_disable_hot_patch));
        ll.addView(subtitle(this, "参数设定"));
        ll.addView(_t = newListItemButton(this, "跳转控制", "跳转自身及第三方Activity控制", "N/A", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Initiator.load("com.tencent.mobileqq.haoliyou.JefsClass") != null) {
                    MainHook.startProxyActivity(v.getContext(), JefsRulesActivity.class);
                } else {
                    Utils.showToast(SettingsActivity.this, TOAST_TYPE_ERROR, "当前版本客户端版本不支持", Toast.LENGTH_SHORT);
                }
            }
        }));
        __jmp_ctl_cnt = _t.findViewById(R_ID_VALUE);
        ll.addView(newListItemSwitchStub(this, "禁用特别关心长震动", "他女朋友都没了他也没开发这个功能", false));
        ll.addView(subtitle(this, "关于"));
        ll.addView(newListItemDummy(this, HostInformationProviderKt.getHostInformationProvider().getHostName(), null, HostInformationProviderKt.getHostInformationProvider().getVersionName() + "(" + HostInformationProviderKt.getHostInformationProvider().getVersionCode() + ")"));
        ll.addView(newListItemDummy(this, "模块版本", null, Utils.QN_VERSION_NAME));
        UpdateCheck uc = new UpdateCheck();
        ll.addView(_t = newListItemButton(this, "检查更新", null, "点击检查", uc));
        uc.setVersionTip(_t);
        ll.addView(newListItemButton(this, "关于模块", null, null, clickToProxyActAction(AboutActivity.class)));
        ll.addView(newListItemButton(this, "用户协议", "《QNotified 最终用户许可协议》与《隐私条款》", null, clickToProxyActAction(EulaActivity.class)));
        ll.addView(newListItemButton(this, "展望未来", "其实都还没写", null, clickToProxyActAction(PendingFuncActivity.class)));
        ll.addView(newListItemButton(this, "特别鸣谢", "感谢卖动绘制图标", null, clickToProxyActAction(LicenseActivity.class)));
        ll.addView(subtitle(this, "调试"));
        ll.addView(newListItemButton(this, "故障排查", null, null, clickToProxyActAction(TroubleshootActivity.class)));
        ll.addView(newListItemButton(this, "Shell.exec", "正常情况下无需使用此功能", null, clickTheComing()));
        ll.addView(newListItemButton(this, "Github", "获取源代码 Bug -> Issue (star)", "ferredoxin/QNotified", clickToUrl("https://github.com/ferredoxin/QNotified")));
        ll.addView(subtitle(this, "本软件为免费软件,请尊重个人劳动成果,严禁倒卖\nLife feeds on negative entropy."));
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        setContentBackgroundDrawable(ResUtils.skin_background);
        setRightButton("更多", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverflowPopupMenu();
            }
        });
        setTitle("高级");
        try {
            getString(R.string.res_inject_success);
        } catch (Resources.NotFoundException e) {
            CustomDialog.createFailsafe(this).setTitle("FATAL Exception").setCancelable(true).setPositiveButton(getString(android.R.string.yes), null)
                    .setNeutralButton("重启" + HostInformationProviderKt.getHostInformationProvider().getHostName(), (dialog, which) -> {
                        try {
                            android.os.Process.killProcess(android.os.Process.myPid());
                        } catch (Throwable e1) {
                            log(e1);
                        }
                    })
                    .setMessage("Resources injection failure!\nApplication may misbehave.\n" + e.toString()
                            + "\n如果您刚刚更新了插件, 您可能需要重启" + HostInformationProviderKt.getHostInformationProvider().getHostName() + "(太/无极阴,应用转生,天鉴等虚拟框架)或者重启手机(EdXp, Xposed, 太极阳), 如果重启手机后问题仍然存在, 请向作者反馈, 并提供详细日志").show();
        }
        return true;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void doOnResume() {
        super.doOnResume();
        ConfigManager cfg = ConfigManager.getDefaultConfig();//改这里的话可能会引发其他问题，所以只把红包和全体改了
        rgbEnabled = cfg.getBooleanOrFalse(qn_enable_fancy_rgb);
        String str = ExfriendManager.getCurrent().getConfig().getString(ConfigItems.qn_muted_at_all);
        int n = 0;
        if (str != null && str.length() > 4) n = str.split(",").length;
        __tv_muted_atall.setText(n + "个群");
        str = ExfriendManager.getCurrent().getConfig().getString(ConfigItems.qn_muted_red_packet);
        n = 0;
        if (str != null && str.length() > 4) n = str.split(",").length;
        __tv_muted_redpacket.setText(n + "个群");
        if (__tv_fake_bat_status != null) {
            FakeBatteryHook bat = FakeBatteryHook.get();
            if (bat.isEnabled()) {
                int cap = bat.getFakeBatteryCapacity();
                boolean c = bat.isFakeBatteryCharging();
                __tv_fake_bat_status.setText(cap + (c ? "%+ " : "% "));
            } else {
                __tv_fake_bat_status.setText("[系统电量]");
            }
        }
        updateRecvRedirectStatus();
        if (__jmp_ctl_cnt != null) {
            int cnt = JumpController.get().getEffectiveRulesCount();
            if (cnt == -1) {
                __jmp_ctl_cnt.setText("[禁用]");
            } else {
                __jmp_ctl_cnt.setText("" + cnt);
            }
        }
        isVisible = true;
        startRgbIfEnabled();
    }

    @Override
    public void doOnPause() {
        isVisible = false;
        super.doOnPause();
    }

    private void startRgbIfEnabled() {
        if (!rgbEnabled || !isVisible) return;
        mRikkaTitle.setText("花Q[狐狸狸魔改版]");
        new Thread(this).start();
    }

    private void stopRgb() {
        isVisible = false;
    }

    public void onAddAccountClick(View v) {
        CustomDialog dialog = CustomDialog.createFailsafe(this);
        Context ctx = dialog.getContext();
        EditText editText = new EditText(ctx);
        editText.setTextSize(16);
        int _5 = dip2px(SettingsActivity.this, 5);
        editText.setPadding(_5, _5, _5, _5);
        ViewCompat.setBackground(editText, new HighContrastBorder());
        LinearLayout linearLayout = new LinearLayout(ctx);
        linearLayout.addView(editText, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, _5 * 2));
        AlertDialog alertDialog = (AlertDialog) dialog
            .setTitle("输入要添加的QQ号")
            .setView(linearLayout)
            .setPositiveButton("添加", null)
            .setNegativeButton("取消", null)
            .create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
            String uinText = editText.getText().toString();
            long uin = -1;
            try {
                uin = Long.parseLong(uinText);
            } catch (NumberFormatException ignored) {
            }
            if (uin < 10000) {
                Toasts.error(SettingsActivity.this, "QQ号无效");
                return;
            }
            boolean success;
            File f = new File(getFilesDir(), "user/u_" + uin + "_t");
            try {
                success = f.createNewFile();
            } catch (IOException e) {
                Toasts.error(SettingsActivity.this, e.toString().replaceAll("java\\.(lang|io)\\.", ""));
                return;
            }
            if (success) {
                Toasts.success(SettingsActivity.this, "已添加");
            } else {
                Toasts.info(SettingsActivity.this, "该账号已存在");
                return;
            }
            alertDialog.dismiss();
        });
    }

    public void onFileRecvRedirectClick(View v) {
        if (v.getId() == R_ID_BTN_FILE_RECV) {
            if (FileRecvRedirect.get().checkPreconditions()) {
                showChangeRecvPathDialog();
            } else {
                new Thread(() -> {
                    doSetupForPrecondition(SettingsActivity.this, FileRecvRedirect.get());
                    runOnUiThread(this::showChangeRecvPathDialog);
                }).start();
            }
        }
    }

    int color;
    int step;//(0-255)
    int stage;//0-5
    private boolean isVisible = false;
    private boolean rgbEnabled = false;
    private TextView mRikkaTitle, mRikkaDesc;
    private Looper mainLooper = Looper.getMainLooper();


    /**
     * 没良心的method
     */
    @Override
    public void run() {
        if (mRikkaTitle != null && mRikkaDesc != null && Looper.myLooper() == mainLooper) {
            if (rgbEnabled) {
                mRikkaTitle.setTextColor(color);
                mRikkaDesc.setTextColor(color);
            } else {
                mRikkaTitle.setText("花Q");
                mRikkaTitle.setTextColor(ResUtils.skin_black);
                mRikkaDesc.setTextColor(ResUtils.skin_gray3);
            }
            return;
        }
        while (isVisible && rgbEnabled) {
            try {
                Thread.sleep(75);
            } catch (InterruptedException ignored) {
            }
            step += 30;
            stage = (stage + step / 256) % 6;
            step = step % 256;
            switch (stage) {
                case 0:
                    color = Color.argb(255, 255, step, 0);//R-- RG-
                    break;
                case 1:
                    color = Color.argb(255, 255 - step, 255, 0);//RG- -G-
                    break;
                case 2:
                    color = Color.argb(255, 0, 255, step);//-G- -GB
                    break;
                case 3:
                    color = Color.argb(255, 0, 255 - step, 255);//-GB --B
                    break;
                case 4:
                    color = Color.argb(255, step, 0, 255);//--B R-B
                    break;
                case 5:
                    color = Color.argb(255, 255, 0, 255 - step);//R-B R--
                    break;
            }
            runOnUiThread(this);
        }
        runOnUiThread(this);
    }


    private void showChangeRecvPathDialog() {
        FileRecvRedirect recv = FileRecvRedirect.get();
        String currPath = recv.getRedirectPath();
        if (currPath == null) currPath = recv.getDefaultPath();
        CustomDialog dialog = CustomDialog.createFailsafe(this);
        Context ctx = dialog.getContext();
        EditText editText = new EditText(ctx);
        editText.setText(currPath);
        editText.setTextSize(16);
        int _5 = dip2px(SettingsActivity.this, 5);
        editText.setPadding(_5, _5, _5, _5);
        ViewCompat.setBackground(editText, new HighContrastBorder());
        LinearLayout linearLayout = new LinearLayout(ctx);
        linearLayout.addView(editText, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, _5 * 2));
        AlertDialog alertDialog = (AlertDialog) dialog
            .setTitle("输入重定向文件夹路径")
            .setView(linearLayout)
            .setPositiveButton("确认并激活", null)
            .setNegativeButton("取消", null)
            .setNeutralButton("使用默认值", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    recv.setEnabled(false);
                    updateRecvRedirectStatus();
                }
                })
                .create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = editText.getText().toString();
                if (path.equals("")) {
                    showToast(SettingsActivity.this, TOAST_TYPE_ERROR, "请输入路径", Toast.LENGTH_SHORT);
                    return;
                }
                if (!path.startsWith("/")) {
                    showToast(SettingsActivity.this, TOAST_TYPE_ERROR, "请输入完整路径(以\"/\"开头)", Toast.LENGTH_SHORT);
                    return;
                }
                File f = new File(path);
                if (!f.exists() || !f.isDirectory()) {
                    showToast(SettingsActivity.this, TOAST_TYPE_ERROR, "文件夹不存在", Toast.LENGTH_SHORT);
                    return;
                }
                if (!f.canWrite()) {
                    showToast(SettingsActivity.this, TOAST_TYPE_ERROR, "文件夹无访问权限", Toast.LENGTH_SHORT);
                    return;
                }
                if (!path.endsWith("/")) path += "/";
                recv.setRedirectPathAndEnable(path);
                updateRecvRedirectStatus();
                alertDialog.dismiss();
            }
        });
    }

    private void updateRecvRedirectStatus() {
        FileRecvRedirect recv = FileRecvRedirect.get();
        if (recv.isEnabled()) {
            __recv_status.setText("[已启用]");
            __recv_desc.setText(recv.getRedirectPath());
        } else {
            __recv_status.setText("[禁用]");
            __recv_desc.setText(recv.getDefaultPath());
        }
    }

    private void showOverflowPopupMenu() {
        Utils.showToast(this, TOAST_TYPE_INFO, "没有更多了", Toast.LENGTH_SHORT);
    }

}
