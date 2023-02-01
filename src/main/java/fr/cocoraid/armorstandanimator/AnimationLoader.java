package fr.cocoraid.armorstandanimator;

import com.google.common.collect.Lists;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.joml.Vector3f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.joml.Matrix4f;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class AnimationLoader {

    private final Skeleton skeleton = new Skeleton();


    private static final Map<String, MixamoTag> MIXAMO_TAG_MAP = new HashMap<>();

    static {
        MIXAMO_TAG_MAP.put(SkeletonType.HEAD.getName(), new MixamoTag(
                SkeletonType.HEAD.getOutputKey()));
        MIXAMO_TAG_MAP.put(SkeletonType.BODY.getName(), new MixamoTag(
                SkeletonType.BODY.getOutputKey()));
        MIXAMO_TAG_MAP.put(SkeletonType.LEFT_ARM.getName(), new MixamoTag(
                SkeletonType.LEFT_ARM.getOutputKey()));
        MIXAMO_TAG_MAP.put(SkeletonType.RIGHT_ARM.getName(), new MixamoTag(
                SkeletonType.RIGHT_ARM.getOutputKey()));
        MIXAMO_TAG_MAP.put(SkeletonType.LEFT_LEG.getName(), new MixamoTag(
                SkeletonType.LEFT_LEG.getOutputKey()));
        MIXAMO_TAG_MAP.put(SkeletonType.RIGHT_LEG.getName(), new MixamoTag(
                SkeletonType.RIGHT_LEG.getOutputKey()));
        MIXAMO_TAG_MAP.put(SkeletonType.LEFT_UP_LEG.getName(), new MixamoTag(
                SkeletonType.LEFT_UP_LEG.getOutputKey()));
        MIXAMO_TAG_MAP.put(SkeletonType.RIGHT_UP_LEG.getName(), new MixamoTag(
                SkeletonType.RIGHT_UP_LEG.getOutputKey()));
        MIXAMO_TAG_MAP.put(SkeletonType.LEFT_FORE_ARM.getName(), new MixamoTag(
                SkeletonType.LEFT_FORE_ARM.getOutputKey()));
        MIXAMO_TAG_MAP.put(SkeletonType.RIGHT_FORE_ARM.getName(), new MixamoTag(
                SkeletonType.RIGHT_FORE_ARM.getOutputKey()));
    }

    private LinkedList<AngleTransform> transforms = new LinkedList<>();


    public void load() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        InputStream is = ArmorstandAnimator.class.getResourceAsStream("/model.dae");
        Document document = builder.parse(is);
        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement();
        System.out.println(root.getNodeName());

        System.out.println("Loading animations....");

        NodeList animationTags = document.getElementsByTagName("animation");

        System.out.println("============================");


        getChilds(animationTags).forEach((animationNode) -> {

            if (!animationNode.hasAttributes()) return;
            String animationId = animationNode.getAttributes().getNamedItem("id").getNodeValue();
            if (animationId == null) return;
            if (!MIXAMO_TAG_MAP.containsKey(animationId)) return;

            //if (!SkeletonType.BODY.getName().equals(animationId)) return;
            //   && !SkeletonType.RIGHT_FORE_ARM.getName().equals(animationId))

            System.out.println("Found animation: " + animationId);

            LinkedList<Float> matrixes = new LinkedList<>();
            NodeList floatArrayTags = document.getElementsByTagName("float_array");
            getChilds(floatArrayTags).stream()
                    .filter((floatArrayNode) -> {
                        if (!floatArrayNode.hasAttributes()) return false;
                        String floatArrayId = floatArrayNode.getAttributes().getNamedItem("id").getNodeValue();
                        if (!floatArrayId.equals(MIXAMO_TAG_MAP.get(animationId).getAnimationQuaternionKey()))
                            return false;
                        if (floatArrayId == null) return false;
                        if (!isAnimationQuaternion(floatArrayId)) return false;
                        return true;
                    }).forEach(floatArrayNode -> {
                        String floatArrayId = floatArrayNode.getAttributes().getNamedItem("id").getNodeValue();
                        // output is quaternion
                        if (floatArrayId.contains("output")) {
                            matrixes.addAll(getFloatList(floatArrayNode));
                        }

                    });


            List<List<Float>> quaternionsList = Lists.partition(matrixes, 16);
            quaternionsList.forEach(list -> {
                float[] matrix = new float[16];
                for (int i = 0; i < list.size(); i++) {
                    matrix[i] = list.get(i);
                }
                Matrix4f mat = new Matrix4f();
                mat.set(matrix);
                Vector3f vector = mat.getEulerAnglesZYX(new Vector3f());

                SkeletonType skeletonType = SkeletonType.getSkeletonTypeByName(animationId);

                EulerAngle eulerAngle;
                if (skeletonType == SkeletonType.BODY) {
                    eulerAngle = new EulerAngle(
                            vector.x,
                            vector.y,
                            vector.z
                    );
                    transforms.add(new AngleTransform(Math.toDegrees(eulerAngle.getX()),
                            Math.toDegrees(eulerAngle.getY()),
                            Math.toDegrees(eulerAngle.getZ())));
                } else {
                    eulerAngle = new EulerAngle(
                            ((skeletonType == SkeletonType.HEAD ? 1 : -1) * vector.x),
                            vector.y,
                            vector.z
                    );
                }

                skeleton.getSkeletonParts().get(skeletonType).getFrameAnimations().add(eulerAngle);


            });

        });

        System.out.println("size " + transforms.size());
        for (int i = 0; i < transforms.size(); i++) {
            AngleTransform transform = transforms.get(i);
            System.out.println("Frame " + i + ": " + transform);
        }

    }


    private Vector interpolate(float scalarValue, Vector v1, Vector v2) {
        Vector result = new Vector();
        result.setX(v2.getX() + scalarValue * v1.getX());
        result.setY(v2.getY() + scalarValue * v1.getY());
        result.setZ(v2.getZ() + scalarValue * v1.getZ());
        return result;
    }

    private static boolean isAnimationQuaternion(String id) {
        return MIXAMO_TAG_MAP.values().stream().anyMatch(mixamoTag -> mixamoTag.getAnimationQuaternionKey().equalsIgnoreCase(id));
    }


    private LinkedList<Float> getFloatList(Node node) {
        String arrayFloatInput = node.getTextContent();
        //remove all space at the begining
        arrayFloatInput = arrayFloatInput.replaceAll("^\\s+", "");
        // clear all ligne breaks without removing the spaces
        arrayFloatInput = arrayFloatInput.replaceAll("\\r\\n|\\r|\\n", " ");
        String[] arrayFloatInputSplit = arrayFloatInput.split(" ");
        LinkedList<Float> timeArray = new LinkedList<>();
        for (String s : arrayFloatInputSplit) {
            timeArray.add(Float.parseFloat(s));
        }
        return timeArray;
    }

    private List<Node> getChilds(NodeList list) {
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;
            nodes.add(node);
        }
        return nodes;
    }


    public Skeleton getSkeleton() {
        return skeleton;
    }


}
