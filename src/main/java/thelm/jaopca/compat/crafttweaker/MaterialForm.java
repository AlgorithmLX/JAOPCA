package thelm.jaopca.compat.crafttweaker;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.impl.item.MCItemStack;
import com.blamejared.crafttweaker.impl.tag.MCTag;
import com.blamejared.crafttweaker.impl.tag.manager.TagManager;
import com.blamejared.crafttweaker.impl.tag.manager.TagManagerItem;
import com.google.common.collect.TreeBasedTable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import thelm.jaopca.api.blocks.IBlockProvider;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.MiscHelper;

@ZenRegister
@ZenCodeType.Name("mods.jaopca.MaterialForm")
public class MaterialForm {

	private static final TreeBasedTable<IForm, IMaterial, MaterialForm> MATERIAL_FORM_WRAPPERS = TreeBasedTable.create();
	private final IMaterialFormInfo info;

	public static MaterialForm getMaterialFormWrapper(IForm form, IMaterial material) {
		MaterialForm materialForm = MATERIAL_FORM_WRAPPERS.get(form, material);
		if(materialForm == null) {
			IMaterialFormInfo info = form.getType().getMaterialFormInfo(form, material);
			materialForm = new MaterialForm(info);
		}
		return materialForm;
	}

	private MaterialForm(IMaterialFormInfo info) {
		this.info = info;
	}

	public IMaterialFormInfo getInternal() {
		return info;
	}

	@ZenCodeType.Getter("form")
	public Form getForm() {
		return Form.getFormWrapper(info.getMaterialForm().getForm());
	}

	@ZenCodeType.Getter("material")
	public Material getMaterial() {
		return Material.getMaterialWrapper(info.getMaterialForm().getMaterial());
	}

	@ZenCodeType.Method
	public MCTag asTag() {
		return asTag(TagManagerItem.INSTANCE);
	}

	@ZenCodeType.Method
	public MCTag asTag(TagManager manager) {
		return new MCTag(MiscHelper.INSTANCE.getTagLocation(
				info.getMaterialForm().getForm().getSecondaryName(), info.getMaterialForm().getMaterial().getName(),
				info.getMaterialForm().getForm().getTagSeparator()),
				manager);
	}

	@ZenCodeType.Method
	public IItemStack asItemStack(int count) {
		if(!(info instanceof IItemProvider)) {
			return null;
		}
		return new MCItemStack(new ItemStack((IItemProvider)info, count));
	}

	@ZenCodeType.Method
	public IItemStack asItemStack() {
		return asItemStack(1);
	}

	@ZenCodeType.Method
	public Block asBlock() {
		if(!(info instanceof IBlockProvider)) {
			return null;
		}
		return ((IBlockProvider)info).asBlock();
	}

	@ZenCodeType.Method
	public BlockState asBlockState() {
		if(!(info instanceof IBlockProvider)) {
			return null;
		}
		return (((IBlockProvider)info).asBlock().getDefaultState());
	}
}
