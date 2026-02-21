package io.github.kosianodangoo.trialmonolith.transformer;

import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Field;
import java.util.Map;

public class GenericTransformer {
    static String ENTITY_METHODS = "io/github/kosianodangoo/trialmonolith/transformer/method/EntityMethods";
    static String ENTITY_HELPER = "io/github/kosianodangoo/trialmonolith/common/helper/EntityHelper";
    static boolean initialized = false;

    @SuppressWarnings("unused")
    public enum Phase {
        ITransformationService, ILaunchPluginServiceBefore, PostMixin ,ILaunchPluginService, ClassFileTransformer
    }

    static {
        initialize();
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        try {
            ILaunchPluginService plugin = new TheTrialMonolithPlugin();

            Field field = Launcher.class.getDeclaredField("launchPlugins");
            field.setAccessible(true);
            LaunchPluginHandler pluginHandler = (LaunchPluginHandler) field.get(Launcher.INSTANCE);
            field = LaunchPluginHandler.class.getDeclaredField("plugins");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, ILaunchPluginService> map = (Map<String, ILaunchPluginService>) field.get(pluginHandler);
            map.put(plugin.name(), plugin);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            TheTrialMonolith.LOGGER.error(e.toString());
        }
        initialized = true;
    }

    public static boolean transform(Phase phase, ClassNode classNode) {
        if (classNode.name.startsWith("io/github/kosianodangoo/trialmonolith/transformer"))
            return false;
        boolean modified = false;

        for (MethodNode method : classNode.methods) {
            for (AbstractInsnNode insn : method.instructions) {
                if (insn instanceof MethodInsnNode methodInsn) {
                    if (insn.getOpcode() == Opcodes.INVOKEVIRTUAL || insn.getOpcode() == Opcodes.INVOKEINTERFACE) {
                        if (isSameMethod(methodInsn.owner, methodInsn, "net/minecraft/world/entity/LivingEntity", "m_21223_", "getHealth", "()F", false)) {
                            method.instructions.insertBefore(methodInsn, new InsnNode(Opcodes.DUP));
                            method.instructions.insert(methodInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "getHealth", "(Lnet/minecraft/world/entity/LivingEntity;F)F"));
                            method.maxStack += 1;
                            modified = true;
                        } else if (isSameMethod(methodInsn.owner, methodInsn, "net/minecraft/world/entity/LivingEntity", "m_21224_", "isDeadOrDying", "()Z", false)) {
                            method.instructions.insertBefore(methodInsn, new InsnNode(Opcodes.DUP));
                            method.instructions.insert(methodInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "isDeadOrDying", "(Lnet/minecraft/world/entity/LivingEntity;Z)Z"));
                            method.maxStack += 1;
                            modified = true;
                        } else if (isSameMethod(methodInsn.owner, methodInsn, "net/minecraft/world/entity/Entity", "m_6084_", "isAlive", "()Z", false)) {
                            method.instructions.insertBefore(methodInsn, new InsnNode(Opcodes.DUP));
                            method.instructions.insert(methodInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "isAlive", "(Lnet/minecraft/world/entity/Entity;Z)Z"));
                            method.maxStack += 1;
                            modified = true;
                        } else if (isSameMethod(methodInsn.owner, methodInsn, "net/minecraft/world/entity/Entity", "m_240725_", "isRemoved", "()Z", false)) {
                            method.instructions.insertBefore(methodInsn, new InsnNode(Opcodes.DUP));
                            method.instructions.insert(methodInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "isRemoved", "(Lnet/minecraft/world/entity/Entity;Z)Z"));
                            method.maxStack += 1;
                            modified = true;
                        } else if (isSameMethod(methodInsn.owner, methodInsn, "net/minecraft/world/entity/Entity", "m_146911_", "getRemovalReason", "()Lnet/minecraft/world/entity/Entity$RemovalReason;", false)) {
                            method.instructions.insertBefore(methodInsn, new InsnNode(Opcodes.DUP));
                            method.instructions.insert(methodInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "getRemovalReason", "(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$RemovalReason;)Lnet/minecraft/world/entity/Entity$RemovalReason;"));
                            method.maxStack += 1;
                            modified = true;
                        } else if (phase == Phase.ILaunchPluginServiceBefore &&
                                isSameMethod(classNode.name, method, "net/minecraft/world/level/entity/EntityTickList", "m_156910_", "forEach", "(Ljava/util/function/Consumer;)V", false) &&
                                isSameMethod(methodInsn.owner, methodInsn, "java/util/function/Consumer", "accept", "accept", "(Ljava/lang/Object;)V", true)) {
                            LabelNode skipLabelNode = new LabelNode(new Label());
                            LabelNode endLabelNode = new LabelNode(new Label());
                            InsnList insnListB = new InsnList();
                            insnListB.add(new InsnNode(Opcodes.DUP));
                            insnListB.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_HELPER, "isSoulProtected", "(Lnet/minecraft/world/entity/Entity;)Z"));
                            insnListB.add(new JumpInsnNode(Opcodes.IFGT, skipLabelNode));
                            InsnList insnListA = new InsnList();
                            insnListA.add(new JumpInsnNode(Opcodes.GOTO, endLabelNode));
                            insnListA.add(skipLabelNode);
                            insnListA.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "tickOverride", "(Ljava/util/function/Consumer;Lnet/minecraft/world/entity/Entity;)V"));
                            insnListA.add(endLabelNode);
                            method.instructions.insertBefore(methodInsn, insnListB);
                            method.instructions.insert(methodInsn, insnListA);
                            method.maxStack += 1;
                            modified = true;
                        }
                    }
                }
                if (insn.getOpcode() == Opcodes.FRETURN) {
                    if (isSameMethod(classNode.name, method, "net/minecraft/world/entity/LivingEntity", "m_21223_", "getHealth", "()F", false)) {
                        InsnList insnList = new InsnList();
                        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "getHealth", "(FLnet/minecraft/world/entity/LivingEntity;)F"));
                        method.instructions.insertBefore(insn, insnList);
                        method.maxStack += 1;
                        modified = true;
                    }
                } else if (insn.getOpcode() == Opcodes.IRETURN) {
                    if (isSameMethod(classNode.name, method, "net/minecraft/world/entity/LivingEntity", "m_21224_", "isDeadOrDying", "()Z", false)) {
                        InsnList insnList = new InsnList();
                        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "isDeadOrDying", "(ZLnet/minecraft/world/entity/LivingEntity;)Z"));
                        method.instructions.insertBefore(insn, insnList);
                        method.maxStack += 1;
                        modified = true;
                    } else if (isSameMethod(classNode.name, method, "net/minecraft/world/entity/Entity", "m_6084_", "isAlive", "()Z", false)) {
                        InsnList insnList = new InsnList();
                        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "isAlive", "(ZLnet/minecraft/world/entity/Entity;)Z"));
                        method.instructions.insertBefore(insn, insnList);
                        method.maxStack += 1;
                        modified = true;
                    } else if (isSameMethod(classNode.name, method, "net/minecraft/world/entity/Entity", "m_213877_", "isRemoved", "()Z", false)) {
                        InsnList insnList = new InsnList();
                        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "isRemoved", "(ZLnet/minecraft/world/entity/Entity;)Z"));
                        method.instructions.insertBefore(insn, insnList);
                        method.maxStack += 1;
                        modified = true;
                    }
                } else if (insn.getOpcode() == Opcodes.ARETURN) {
                    if (isSameMethod(classNode.name, method, "net/minecraft/world/entity/Entity", "m_146911_", "getRemovalReason", "()Lnet/minecraft/world/entity/Entity$RemovalReason;", false)) {
                        InsnList insnList = new InsnList();
                        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "getRemovalReason", "(Lnet/minecraft/world/entity/Entity$RemovalReason;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/entity/Entity$RemovalReason;"));
                        method.instructions.insertBefore(insn, insnList);
                        method.maxStack += 1;
                        modified = true;
                    }
                }
            }
            if (isSameMethod(classNode.name, method, "net/minecraft/world/entity/LivingEntity", "m_21223_", "getHealth", "()F", false)) {
                injectHead(method,
                        new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "shouldReplaceHealthMethod", "(Lnet/minecraft/world/entity/Entity;)Z", false),
                        new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "replaceGetHealth", "(Lnet/minecraft/world/entity/LivingEntity;)F", false),
                        new InsnNode(Opcodes.FRETURN));
                method.maxStack += 1;
                modified = true;
            } else if (isSameMethod(classNode.name, method, "net/minecraft/world/entity/LivingEntity", "m_21224_", "isDeadOrDying", "()Z", false)) {
                injectHead(method,
                        new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "shouldReplaceHealthMethod", "(Lnet/minecraft/world/entity/Entity;)Z", false),
                        new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "replaceIsDeadOrDying", "(Lnet/minecraft/world/entity/Entity;)Z", false),
                        new InsnNode(Opcodes.IRETURN));
                method.maxStack += 1;
                modified = true;
            } else if (isSameMethod(classNode.name, method, "net/minecraft/world/entity/Entity", "m_6084_", "isAlive", "()Z", false)) {
                injectHead(method,
                        new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "shouldReplaceHealthMethod", "(Lnet/minecraft/world/entity/Entity;)Z", false),
                        new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "replaceIsAlive", "(Lnet/minecraft/world/entity/Entity;)Z", false),
                        new InsnNode(Opcodes.IRETURN));
                method.maxStack += 1;
                modified = true;
            }
        }
        return modified;
    }

    public static void injectHead(MethodNode method, MethodInsnNode judgeMethod, MethodInsnNode replaceMethod, InsnNode returnInsn) {
        LabelNode skipLabelNode = new LabelNode(new Label());
        InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(judgeMethod);
        insnList.add(new JumpInsnNode(Opcodes.IFLE, skipLabelNode));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(replaceMethod);
        insnList.add(returnInsn);
        insnList.add(skipLabelNode);
        method.instructions.insertBefore(method.instructions.getFirst(), insnList);
    }

    public static boolean isSameMethod(String owner, MethodInsnNode methodInsn, String superClass, String obfName, String name, String desc, boolean isInterface) {
        if ((!obfName.equals(methodInsn.name) && !name.equals(methodInsn.name)) || !desc.equals(methodInsn.desc)) {
            return false;
        }

        return isSubclass(owner, superClass, isInterface);
    }

    public static boolean isSameMethod(String owner, MethodNode method, String superClass, String obfName, String name, String desc, boolean isInterface) {
        if ((!obfName.equals(method.name) && !name.equals(method.name)) || !desc.equals(method.desc)) {
            return false;
        }

        return isSubclass(owner, superClass, isInterface);
    }

    public static boolean isSubclass(String className, String superClass, boolean isInterface) {
        if (className.equals(superClass) || superClass.equals("java/lang/Object")) {
            return true;
        }

        if (className.equals("java/lang/Object")) {
            return false;
        }

        String currentName = className;

        while (!currentName.equals("java/lang/Object")) {
            try {
                ClassReader classReader = new ClassReader(currentName);
                currentName = classReader.getSuperName();
                if (currentName.equals(superClass)) {
                    return true;
                }
                if (isInterface) {
                    for (String interfaceName : classReader.getInterfaces()) {
                        if (isSubclass(interfaceName, superClass, true)) {
                            return true;
                        }
                    }
                }
            } catch (Throwable e) {
                return false;
            }
        }

        return false;
    }
}
