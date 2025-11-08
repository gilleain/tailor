package aigen.script;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aigen.condition.AngleBoundCondition;
import aigen.condition.Condition;
import aigen.condition.DistanceBoundCondition;
import aigen.condition.HBondCondition;
import aigen.condition.PropertyCondition;
import aigen.condition.TorsionBoundCondition;
import aigen.description.AtomDescription;
import aigen.description.ChainDescription;
import aigen.description.Description;
import aigen.description.ResidueDescription;
import aigen.measure.AngleMeasure;
import aigen.measure.DistanceMeasure;
import aigen.measure.HBondMeasure;
import aigen.measure.Measure;
import aigen.measure.PropertyMeasure;
import aigen.measure.TorsionMeasure;
import aigen.run.Pipe;
import aigen.run.Run;

/**
 * Main script parser and translator
 * 
 * First, create the ANTLR4 grammar file (MotifScript.g4):
 * 
 * grammar MotifScript;
 * 
 * script
 *     : motifDescription filepath filenames measures EOF
 *     ;
 * 
 * motifDescription
 *     : 'motifDescription' '=' predefinedMotif
 *     | 'motifDescription' '=' definedMotif
 *     ;
 * 
 * predefinedMotif
 *     : 'PREDEFINED' QUOTED_WORD
 *     ;
 * 
 * definedMotif
 *     : level+
 *     ;
 * 
 * level
 *     : levelName property+
 *     ;
 * 
 * levelName
 *     : 'Chain' | 'Residue' | 'Atom'
 *     ;
 * 
 * property
 *     : attributeName '=' attributeValue
 *     ;
 * 
 * attributeName
 *     : 'chainID' | 'chainType' | 'position' | 'resname'
 *     | 'hydrogen-bond' | 'torsion-bound' | 'distance-bound' | 'angle-bound'
 *     ;
 * 
 * attributeValue
 *     : selectionList? value+
 *     ;
 * 
 * selectionList
 *     : '(' selection+ ')'
 *     ;
 * 
 * selection
 *     : INTEGER '.' WORD
 *     ;
 * 
 * value
 *     : QUOTED_WORD | INTEGER | REAL | numberWithRange
 *     ;
 * 
 * numberWithRange
 *     : anyNum '+/-' anyNum
 *     ;
 * 
 * anyNum
 *     : REAL | INTEGER
 *     ;
 * 
 * filepath
 *     : 'filepath' '=' path
 *     ;
 * 
 * path
 *     : UNIX_PATH | WIN_PATH | '.'
 *     ;
 * 
 * filenames
 *     : 'filenames' '=' '[' WORD* ']'
 *     ;
 * 
 * measures
 *     : 'measures' '=' measure+
 *     ;
 * 
 * measure
 *     : geometricMeasure | propertyMeasure
 *     ;
 * 
 * geometricMeasure
 *     : geometricMeasureType selectionList
 *     ;
 * 
 * geometricMeasureType
 *     : 'Distance' | 'Angle' | 'Torsion' | 'HBond'
 *     ;
 * 
 * propertyMeasure
 *     : 'Property' INTEGER WORD valueType
 *     ;
 * 
 * valueType
 *     : 'int' | 'float' | 'str'
 *     ;
 * 
 * QUOTED_WORD : '\'' WORD '\'' ;
 * WORD : [a-zA-Z0-9]+ ;
 * INTEGER : '-'? [0-9]+ ;
 * REAL : INTEGER '.' [0-9]* ;
 * UNIX_PATH : ('~' | '.') ('/' WORD)+ '/'? ;
 * WIN_PATH : [a-zA-Z] ':\\\\' (WORD '\\')+ WORD? ;
 * COMMENT : '#' ~[\r\n]* -> skip ;
 * WS : [ \t\r\n]+ -> skip ;
 */
public class Script {
    
    // Type mappings
    private static final Map<String, Class<?>> TYPE_DICT = Map.of(
        "str", String.class,
        "int", Integer.class,
        "float", Double.class
    );
    
    // Level name to description class mapping
    private static final Map<String, Class<? extends Description>> LEVEL_NAME_DICT = Map.of(
        "Chain", ChainDescription.class,
        "Residue", ResidueDescription.class,
        "Atom", AtomDescription.class
    );
    
    // Description to sub-description mapping
    private static final Map<Class<? extends Description>, Class<? extends Description>> 
        DESC_TO_SUB_DESC_MAP = Map.of(
            ChainDescription.class, ResidueDescription.class,
            ResidueDescription.class, AtomDescription.class
        );
    
    // Keyword to condition mapping
    private static final Map<String, Class<? extends Condition>> KEYWORD_TO_CONDITION_MAP = Map.of(
        "chainID", PropertyCondition.class,
        "chainType", PropertyCondition.class,
        "position", PropertyCondition.class,
        "resname", PropertyCondition.class,
        "distance-bound", DistanceBoundCondition.class,
        "angle-bound", AngleBoundCondition.class,
        "torsion-bound", TorsionBoundCondition.class,
        "hydrogen-bond", HBondCondition.class
    );
    
    // Measure type mapping
    private static final Map<String, Class<? extends Measure>> MEASURE_DICT = Map.of(
        "Property", PropertyMeasure.class,
        "Distance", DistanceMeasure.class,
        "Angle", AngleMeasure.class,
        "Torsion", TorsionMeasure.class,
        "HBond", HBondMeasure.class
    );
    
    /**
     * Parse and execute a script file
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Script <script-file>");
            System.exit(1);
        }
        
        try {
            String fileContent = new String(Files.readAllBytes(Paths.get(args[0])));
            System.out.println(fileContent);
            
            ParsedScript parsed = parseScript(fileContent);
            Run run = translate(parsed);
            run.run();
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Parse script content (simplified parser without full ANTLR4)
     * In a real implementation, you would use ANTLR4-generated parser
     */
    public static ParsedScript parseScript(String content) {
        // This is a simplified manual parser
        // In production, you'd use ANTLR4 generated classes
        ParsedScript result = new ParsedScript();
        
        String[] lines = content.split("\n");
        StringBuilder currentSection = new StringBuilder();
        String currentKey = null;
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            
            if (line.startsWith("motifDescription")) {
                currentKey = "motifDescription";
                currentSection = new StringBuilder(line);
            } else if (line.startsWith("filepath")) {
                currentKey = "filepath";
                String[] parts = line.split("=");
                result.filepath = parts[1].trim();
            } else if (line.startsWith("filenames")) {
                currentKey = "filenames";
                String[] parts = line.split("=");
                String fileStr = parts[1].trim().replaceAll("[\\[\\]]", "");
                if (!fileStr.isEmpty()) {
                    result.filenames = Arrays.asList(fileStr.split("\\s+"));
                }
            } else if (line.startsWith("measures")) {
                currentKey = "measures";
                currentSection = new StringBuilder(line);
            } else if (currentKey != null) {
                currentSection.append(" ").append(line);
            }
        }
        
        return result;
    }
    
    /**
     * Translate parsed script into a Run object
     */
    public static Run translate(ParsedScript parsed) {
        Description motifDescription;
        
        if (parsed.motifDescription != null && parsed.motifDescription.startsWith("PREDEFINED")) {
            motifDescription = lookupMotif(parsed.motifDescription);
        } else {
            motifDescription = makeMotif(parsed.motifDescriptionLevels);
        }
        
        List<String> filenames = parsed.filenames != null ? 
            parsed.filenames : new ArrayList<>();
        
        List<Measure> measureList = makeMeasures(motifDescription, parsed.measures);
        
        List<Pipe> pipes = List.of(new Pipe(motifDescription, measureList, System.out));
        
        return new Run(pipes, parsed.filepath, filenames);
    }
    
    /**
     * Look up a predefined motif description
     */
    public static Description lookupMotif(String motifDescriptionText) {
        String name = motifDescriptionText.split("'")[1];
        return PredefinedDescriptions.get(name);
    }
    
    /**
     * Create a motif description from parsed levels
     */
    public static Description makeMotif(List<LevelText> levelTexts) {
        Description motifDescription = null;
        Description currentLevel = null;
        Class<? extends Description> currentLevelSubType = null;
        
        // Create the Description hierarchy with PropertyConditions
        for (LevelText levelText : levelTexts) {
            try {
                // Create a Description object of the appropriate type
                Class<? extends Description> levelClass = LEVEL_NAME_DICT.get(levelText.levelName);
                Description level = levelClass.getDeclaredConstructor(Map.class)
                    .newInstance(new HashMap<>());
                
                // Add to motif description
                if (motifDescription == null) {
                    motifDescription = level;
                    currentLevel = motifDescription;
                    currentLevelSubType = DESC_TO_SUB_DESC_MAP.get(levelClass);
                } else {
                    if (level.getClass() == currentLevelSubType) {
                        currentLevel.addSubDescription(level);
                    }
                }
                
                // Add backbone atoms for Residue level
                if ("Residue".equals(levelText.levelName)) {
                    for (String name : Arrays.asList("N", "CA", "C", "O")) {
                        ((ResidueDescription) level).addAtom(name);
                    }
                }
                
                // Setup PropertyConditions
                for (PropertyText prop : levelText.properties) {
                    String key = prop.attributeName;
                    if (KEYWORD_TO_CONDITION_MAP.get(key) == PropertyCondition.class) {
                        level.addPropertyCondition(key, prop.values.get(0));
                    }
                }
                
            } catch (Exception e) {
                throw new RuntimeException("Error creating description: " + e.getMessage(), e);
            }
        }
        
        // Second pass: create Conditions that rely on the Description tree
        for (LevelText levelText : levelTexts) {
            for (PropertyText prop : levelText.properties) {
                String key = prop.attributeName;
                
                Class<? extends Condition> conditionClass = KEYWORD_TO_CONDITION_MAP.get(key);
                if (conditionClass != null && conditionClass != PropertyCondition.class) {
                    try {
                        List<Object> params = makeSelections(motifDescription, prop.selections);
                        params.addAll(prop.values.subList(prop.selections.size(), prop.values.size()));
                        
                        // Create condition instance
                        Condition condition = createCondition(conditionClass, params);
                        motifDescription.addCondition(condition);
                        
                    } catch (Exception e) {
                        throw new RuntimeException("Error creating condition: " + e.getMessage(), e);
                    }
                }
            }
        }
        
        motifDescription.setName("default");
        return motifDescription;
    }
    
    /**
     * Create selections from motif definition
     */
    public static List<Object> makeSelections(Description motifDefinition, 
                                             List<Selection> selectionTexts) {
        List<Object> selectionList = new ArrayList<>();
        
        // XXX TODO - how do we know this works?
        ChainDescription chainDescription = (ChainDescription) motifDefinition;
        
        for (Selection selection : selectionTexts) {
            int residueNumber = selection.residueNumber;
            String atomName = selection.atomName;
            
            ResidueDescription selectedResidue = chainDescription.selectResidue(residueNumber);
            Description selectedAtom = selectedResidue.selectAtom(atomName);
            
            selectionList.add(selectedAtom);
        }
        
        return selectionList;
    }
    
    /**
     * Create measures from parsed measure text
     */
    public static List<Measure> makeMeasures(Description motifDefinition, 
                                            List<MeasureText> measureTexts) {
        List<Measure> measures = new ArrayList<>();
        
        // XXX TODO - how do we know this works?
        ChainDescription chainDescription = (ChainDescription) motifDefinition;
        
        for (MeasureText measureText : measureTexts) {
            try {
                if ("Property".equals(measureText.measureType)) {
                    PropertyMeasureText pmt = (PropertyMeasureText) measureText;
                    Description selected = chainDescription.selectResidue(pmt.residueNumber);
                    Class valueType = TYPE_DICT.get(pmt.valueType);
                    
                    Measure measure = new PropertyMeasure(selected, pmt.property, valueType);
                    measures.add(measure);
                    
                } else {
                    GeometricMeasureText gmt = (GeometricMeasureText) measureText;
                    List<Object> selections = makeSelections(motifDefinition, gmt.selections);
                    
                    Class<? extends Measure> measureClass = MEASURE_DICT.get(gmt.measureType);
                    Measure measure = createMeasure(measureClass, selections);
                    measures.add(measure);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error creating measure: " + e.getMessage(), e);
            }
        }
        
        return measures;
    }
    
    /**
     * Helper method to create condition instances
     */
    private static Condition createCondition(Class<? extends Condition> conditionClass, 
                                            List<Object> params) throws Exception {
        // Use reflection to find appropriate constructor
        Class<?>[] paramTypes = params.stream()
            .map(Object::getClass)
            .toArray(Class<?>[]::new);
        
        return conditionClass.getDeclaredConstructor(paramTypes)
            .newInstance(params.toArray());
    }
    
    /**
     * Helper method to create measure instances
     */
    private static Measure createMeasure(Class<? extends Measure> measureClass, 
                                        List<Object> params) throws Exception {
        Class<?>[] paramTypes = params.stream()
            .map(Object::getClass)
            .toArray(Class<?>[]::new);
        
        return measureClass.getDeclaredConstructor(paramTypes)
            .newInstance(params.toArray());
    }
}

/**
 * Data classes for parsed script
 */
class ParsedScript {
    public String motifDescription;
    public List<LevelText> motifDescriptionLevels = new ArrayList<>();
    public String filepath;
    public List<String> filenames = new ArrayList<>();
    public List<MeasureText> measures = new ArrayList<>();
}

class LevelText {
    public String levelName;
    public List<PropertyText> properties = new ArrayList<>();
}

class PropertyText {
    public String attributeName;
    public List<Selection> selections = new ArrayList<>();
    public List<Object> values = new ArrayList<>();
}

class Selection {
    public int residueNumber;
    public String atomName;
    
    public Selection(int residueNumber, String atomName) {
        this.residueNumber = residueNumber;
        this.atomName = atomName;
    }
}

abstract class MeasureText {
    public String measureType;
}

class GeometricMeasureText extends MeasureText {
    public List<Selection> selections = new ArrayList<>();
}

class PropertyMeasureText extends MeasureText {
    public int residueNumber;
    public String property;
    public String valueType;
}


class PredefinedDescriptions {
    private static Map<String, Description> descriptions = new HashMap<>();
    
    public static Description get(String name) {
        return descriptions.get(name);
    }
}
