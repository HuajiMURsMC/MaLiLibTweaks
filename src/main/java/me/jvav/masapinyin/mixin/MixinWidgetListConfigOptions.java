package me.jvav.masapinyin.mixin;

import com.google.common.collect.ImmutableList;
import me.jvav.masapinyin.WidgetUtil;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = WidgetListConfigOptions.class, remap = false)
public abstract class MixinWidgetListConfigOptions extends WidgetListConfigOptionsBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption> {

    public MixinWidgetListConfigOptions(int x, int y, int width, int height, int configWidth) {
        super(x, y, width, height, configWidth);
    }


    @Inject(method = "reCreateListEntryWidgets", at = @At(value = "INVOKE", target = "Lfi/dy/masa/malilib/gui/widgets/WidgetListConfigOptionsBase;reCreateListEntryWidgets()V"))
    private void fixMaxLabelWidth(CallbackInfo ci) {
        this.maxLabelWidth = this.getMaxGuiDisplayNameLengthWrapped(this.listContents);
    }

    private int getMaxGuiDisplayNameLengthWrapped(List<GuiConfigsBase.ConfigOptionWrapper> wrappers) {
        int width = 0;

        for (GuiConfigsBase.ConfigOptionWrapper wrapper : wrappers) {
            if (wrapper.getType() == GuiConfigsBase.ConfigOptionWrapper.Type.CONFIG) {
                IConfigBase configBase = wrapper.getConfig();
                if (configBase == null) {
                    continue;
                }
                width = Math.max(width, this.getStringWidth(WidgetUtil.getTranslatedGuiDisplayName(configBase)));
            }
        }
        return width;
    }

    @Inject(method = "getEntryStringsForFilter*", at = @At(value = "HEAD"), cancellable = true)
    private void preGetEntryStringsForFilter(GuiConfigsBase.ConfigOptionWrapper entry, CallbackInfoReturnable<List<String>> cir) {
        IConfigBase config = entry.getConfig();
        if (config != null) {
            if (config instanceof IConfigResettable && ((IConfigResettable) config).isModified()) {
                cir.setReturnValue(ImmutableList.of(WidgetUtil.getTranslatedGuiDisplayName(config).toLowerCase(), config.getName().toLowerCase(), "modified"));
            } else {
                cir.setReturnValue(ImmutableList.of(WidgetUtil.getTranslatedGuiDisplayName(config).toLowerCase(), config.getName().toLowerCase()));
            }
        }
    }

    // fix upper case when search Disable Hotkeys
    @Override
    protected boolean matchesFilter(List<String> entryStrings, String filterText) {
        filterText = filterText.toLowerCase();
        if (filterText.isEmpty()) {
            return true;
        }

        for (String str : entryStrings) {
            if (this.matchesFilter(str, filterText)) {
                return true;
            }
        }
        return false;
    }
}
