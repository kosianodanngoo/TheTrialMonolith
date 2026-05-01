package io.github.kosianodangoo.trialmonolith.transformer;

import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import io.github.kosianodangoo.trialmonolith.TheTrialMonolith;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GenericTransformer {
    static String ENTITY_METHODS = "io/github/kosianodangoo/trialmonolith/transformer/method/EntityMethods";
    public static List<String> exclusivePackages = new ArrayList<>();
    public static List<String> exclusiveInstructionWrappingPackages = new ArrayList<>();
    private static final Map<String, Boolean> distMismatchCache = new ConcurrentHashMap<>();
    private static final List<String> VANILLA_DIST_RESTRICTED_CLIENT_PREFIXES = List.of(
            "net/minecraft/client/",
            "com/mojang/blaze3d/",
            "net/minecraftforge/client/"
    );
    static final String ONLYIN_DESC = Type.getDescriptor(OnlyIn.class);
    static final String FML_DIST = FMLEnvironment.dist.toString();
    static boolean initialized = false;
    static boolean tickInjected = false;

    @SuppressWarnings("unused")
    public enum Phase {
        ILaunchPluginServiceBefore, ITransformationService, PostMixin, ILaunchPluginService, ClassFileTransformer
    }

    static {
        initialize();
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        exclusivePackages.add("io/github/kosianodangoo/trialmonolith/transformer");
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
        if (exclusivePackages.stream().anyMatch(packageName -> classNode.name.startsWith(packageName)))
            return false;
        boolean modified = false;

        boolean shouldWrapInsn = exclusiveInstructionWrappingPackages.stream().noneMatch(packageName -> classNode.name.startsWith(packageName));

        for (MethodNode method : classNode.methods) {
            for (AbstractInsnNode insn : method.instructions) {
                if (insn instanceof MethodInsnNode methodInsn) {
                    if ((insn.getOpcode() == Opcodes.INVOKEVIRTUAL || insn.getOpcode() == Opcodes.INVOKEINTERFACE) && shouldWrapInsn) {
                        if (isSameMethod(methodInsn.owner, methodInsn, "net/minecraft/world/entity/LivingEntity", "m_21223_", "getHealth", "()F", false)) {
                            method.instructions.insertBefore(methodInsn, new InsnNode(Opcodes.DUP));
                            InsnList insnList = new InsnList();
                            insnList.add(new InsnNode(Opcodes.SWAP));
                            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "getHealth", "(FLnet/minecraft/world/entity/LivingEntity;)F"));
                            method.instructions.insert(methodInsn, insnList);
                            method.maxStack += 1;
                            modified = true;
                        } else if (isSameMethod(methodInsn.owner, methodInsn, "net/minecraft/world/entity/LivingEntity", "m_21224_", "isDeadOrDying", "()Z", false)) {
                            method.instructions.insertBefore(methodInsn, new InsnNode(Opcodes.DUP));
                            InsnList insnList = new InsnList();
                            insnList.add(new InsnNode(Opcodes.SWAP));
                            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "isDeadOrDying", "(ZLnet/minecraft/world/entity/LivingEntity;)Z"));
                            method.instructions.insert(methodInsn, insnList);
                            method.maxStack += 1;
                            modified = true;
                        } else if (isSameMethod(methodInsn.owner, methodInsn, "net/minecraft/world/entity/Entity", "m_6084_", "isAlive", "()Z", false)) {
                            method.instructions.insertBefore(methodInsn, new InsnNode(Opcodes.DUP));
                            InsnList insnList = new InsnList();
                            insnList.add(new InsnNode(Opcodes.SWAP));
                            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "isAlive", "(ZLnet/minecraft/world/entity/Entity;)Z"));
                            method.instructions.insert(methodInsn, insnList);
                            method.maxStack += 1;
                            modified = true;
                        } else if (isSameMethod(methodInsn.owner, methodInsn, "net/minecraft/world/entity/Entity", "m_240725_", "isRemoved", "()Z", false)) {
                            method.instructions.insertBefore(methodInsn, new InsnNode(Opcodes.DUP));
                            InsnList insnList = new InsnList();
                            insnList.add(new InsnNode(Opcodes.SWAP));
                            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "isRemoved", "(ZLnet/minecraft/world/entity/Entity;)Z"));
                            method.instructions.insert(methodInsn, insnList);
                            method.maxStack += 1;
                            modified = true;
                        } else if (isSameMethod(methodInsn.owner, methodInsn, "net/minecraft/world/entity/Entity", "m_146911_", "getRemovalReason", "()Lnet/minecraft/world/entity/Entity$RemovalReason;", false)) {
                            method.instructions.insertBefore(methodInsn, new InsnNode(Opcodes.DUP));
                            InsnList insnList = new InsnList();
                            insnList.add(new InsnNode(Opcodes.SWAP));
                            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "getRemovalReason", "(Lnet/minecraft/world/entity/Entity$RemovalReason;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/entity/Entity$RemovalReason;"));
                            method.instructions.insert(methodInsn, insnList);
                            method.maxStack += 1;
                            modified = true;
                        } else if (phase == Phase.ILaunchPluginServiceBefore &&
                                isSameMethod(classNode.name, method, "net/minecraft/world/level/entity/EntityTickList", "m_156910_", "forEach", "(Ljava/util/function/Consumer;)V", false) &&
                                isSameMethod(methodInsn.owner, methodInsn, "java/util/function/Consumer", "accept", "accept", "(Ljava/lang/Object;)V", true)) {
                            LabelNode skipLabelNode = new LabelNode(new Label());
                            LabelNode endLabelNode = new LabelNode(new Label());
                            InsnList insnListB = new InsnList();
                            insnListB.add(new InsnNode(Opcodes.DUP));
                            insnListB.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "shouldOverrideTick", "(Lnet/minecraft/world/entity/Entity;)Z"));
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
            } else if (isSameMethod(classNode.name, method, "net/minecraft/world/entity/Entity", "m_6087_", "isPickable", "()Z", false)) {
                injectHead(method,
                        new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "shouldReplaceIsPickable", "(Lnet/minecraft/world/entity/Entity;)Z", false),
                        new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "replaceIsPickable", "(Lnet/minecraft/world/entity/Entity;)Z", false),
                        new InsnNode(Opcodes.IRETURN));
                method.maxStack += 1;
                modified = true;
            } else if (!tickInjected && phase.ordinal() >= 2 && isSameMethod(classNode.name, method, "net/minecraft/server/level/ServerLevel", "m_8793_", "tick", "(Ljava/util/function/BooleanSupplier;)V", false)) {
                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ENTITY_METHODS, "updateLastTicks", "(Lnet/minecraft/server/level/ServerLevel;)V"));
                method.instructions.insert(insnList);
                method.maxStack += 1;
                tickInjected = true;
                modified = true;
            }
        }

        if (modified &&
                phase == Phase.ILaunchPluginServiceBefore
                && !classNode.name.startsWith("net/minecraft/")
                && !classNode.name.startsWith("net/minecraftforge/")) {
            stripDistMismatchedReferences(classNode);
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

    public static boolean stripDistMismatchedReferences(ClassNode classNode) {
        boolean classModified = false;
        for (MethodNode method : classNode.methods) {
            if ((method.access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE)) != 0) {
                continue;
            }
            String descClient = scanMethodDesc(method.desc);
            if (descClient != null) {
                replaceMethodWithThrow(method, descClient);
                classModified = true;
                TheTrialMonolith.LOGGER.debug("Replaced method body of {}.{}{} (descriptor references {})", classNode.name, method.name, method.desc, descClient);
                continue;
            }

            boolean methodModified = false;
            AbstractInsnNode insn = method.instructions.getFirst();
            while (insn != null) {
                AbstractInsnNode next = insn.getNext();
                String mismatched = findDistMismatchedType(insn);
                if (mismatched != null) {
                    method.instructions.insertBefore(insn, buildThrowSequence(mismatched));
                    method.maxStack += 3;
                    methodModified = true;
                }
                insn = next;
            }
            if (methodModified) {
                classModified = true;
            }
        }
        return classModified;
    }

    private static String findDistMismatchedType(AbstractInsnNode insn) {
        if (insn instanceof TypeInsnNode ti) return scanInternalNameOrDesc(ti.desc);
        if (insn instanceof MethodInsnNode mi) {
            String r = scanInternalNameOrDesc(mi.owner);
            return r != null ? r : scanMethodDesc(mi.desc);
        }
        if (insn instanceof FieldInsnNode fi) {
            String r = scanInternalNameOrDesc(fi.owner);
            return r != null ? r : scanFieldDesc(fi.desc);
        }
        if (insn instanceof MultiANewArrayInsnNode mn) return scanFieldDesc(mn.desc);
        // LDC of class literal puts java/lang/Class<X> on the stack — the frame type is Class, not X,
        // so frame computation never walks X's hierarchy. Stripping LDC sites would unnecessarily
        // fire throws in static initializers that happen to capture Class<X> for metadata.
        return null;
    }

    private static String scanInternalNameOrDesc(String name) {
        if (name == null || name.isEmpty()) return null;
        char c = name.charAt(0);
        if (c == '[' || c == 'L') return scanType(Type.getType(name));
        if (c == '(') return scanMethodDesc(name);
        return matchDist(name);
    }

    private static String scanFieldDesc(String desc) {
        if (desc == null || desc.isEmpty()) return null;
        return scanType(Type.getType(desc));
    }

    private static String scanMethodDesc(String desc) {
        if (desc == null || desc.isEmpty()) return null;
        Type m = Type.getMethodType(desc);
        for (Type arg : m.getArgumentTypes()) {
            String r = scanType(arg);
            if (r != null) return r;
        }
        return scanType(m.getReturnType());
    }

    private static String scanType(Type t) {
        if (t == null) return null;
        int sort = t.getSort();
        if (sort == Type.OBJECT) return matchDist(t.getInternalName());
        if (sort == Type.ARRAY) return scanType(t.getElementType());
        if (sort == Type.METHOD) {
            for (Type arg : t.getArgumentTypes()) {
                String r = scanType(arg);
                if (r != null) return r;
            }
            return scanType(t.getReturnType());
        }
        return null;
    }

    private static String matchDist(String name) {
        if (name == null) return null;
        if (isDistMismatchedHierarchy(name)) return name;
        return null;
    }

    private static boolean isDistMismatchedHierarchy(String internalName) {
        if (internalName == null || internalName.isEmpty()) return false;
        if (internalName.charAt(0) == '[') return false;
        if (internalName.startsWith("java/")) return false;
        // Forge "extension interfaces" are dist-restricted Forge interfaces added to common-side
        // Mojang types (e.g. IForgeBlockAndTintGetter on BlockAndTintGetter). Treating them as
        // dist-mismatched would propagate the flag up to ServerLevel etc. and cause false positives.
        if (internalName.startsWith("net/minecraftforge/client/extensions/")) return false;
        // Vanilla / Forge client packages: physically removed from the production server jar
        // (forge-universal.jar), so getResourceAsStream returns null and @OnlyIn detection
        // can't see them. Catch them via prefix on the dedicated server side.
        if ("DEDICATED_SERVER".equals(FML_DIST)) {
            for (String prefix : VANILLA_DIST_RESTRICTED_CLIENT_PREFIXES) {
                if (internalName.startsWith(prefix)) return true;
            }
        }
        Boolean cached = distMismatchCache.get(internalName);
        if (cached != null) return cached;
        distMismatchCache.put(internalName, false);
        boolean result = computeDistMismatchedHierarchy(internalName);
        distMismatchCache.put(internalName, result);
        return result;
    }

    private static boolean computeDistMismatchedHierarchy(String internalName) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream is = cl.getResourceAsStream(internalName + ".class")) {
            if (is == null) return false;
            ClassReader reader = new ClassReader(is);
            ClassNode node = new ClassNode(Opcodes.ASM9);
            reader.accept(node, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
            if (hasOtherDistOnlyIn(node.visibleAnnotations)) return true;
            if (hasOtherDistOnlyIn(node.invisibleAnnotations)) return true;
            if (node.superName != null && isDistMismatchedHierarchy(node.superName)) return true;
            if (node.interfaces != null) {
                for (String iface : node.interfaces) {
                    if (isDistMismatchedHierarchy(iface)) return true;
                }
            }
            return false;
        } catch (Throwable e) {
            return false;
        }
    }

    private static boolean hasOtherDistOnlyIn(List<AnnotationNode> annotations) {
        if (annotations == null) return false;
        for (AnnotationNode an : annotations) {
            if (!ONLYIN_DESC.equals(an.desc) || an.values == null) continue;
            for (int i = 0; i + 1 < an.values.size(); i += 2) {
                if (!"value".equals(an.values.get(i))) continue;
                Object v = an.values.get(i + 1);
                if (v instanceof String[] arr && arr.length >= 2 && !FML_DIST.equals(arr[1])) return true;
            }
        }
        return false;
    }

    private static InsnList buildThrowSequence(String internalName) {
        InsnList list = new InsnList();
        list.add(new TypeInsnNode(Opcodes.NEW, "java/lang/RuntimeException"));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new LdcInsnNode("Attempted to load class " + internalName + " for invalid dist " + FML_DIST));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false));
        list.add(new InsnNode(Opcodes.ATHROW));
        return list;
    }

    private static void replaceMethodWithThrow(MethodNode method, String internalName) {
        method.instructions.clear();
        method.tryCatchBlocks.clear();
        if (method.localVariables != null) method.localVariables.clear();
        method.instructions.add(buildThrowSequence(internalName));
        method.maxStack = Math.max(method.maxStack, 3);
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

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        while (!currentName.equals("java/lang/Object")) {
            try (InputStream is = classLoader.getResourceAsStream(currentName.concat(".class"))) {
                ClassReader classReader = new ClassReader(Objects.requireNonNull(is));
                ClassNode classNode = new ClassNode(Opcodes.ASM9);
                classReader.accept(classNode, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
                if (classNode.visibleAnnotations != null && classNode.visibleAnnotations.stream().anyMatch(annotationNode -> annotationNode.desc.equals(ONLYIN_DESC) && !((String[]) annotationNode.values.get(annotationNode.values.indexOf("value") + 1))[1].equals(FML_DIST))) {
                    return false;
                }
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
                TheTrialMonolith.LOGGER.error("Failed to find super Class", e);
                return false;
            }
        }

        return false;
    }
}
